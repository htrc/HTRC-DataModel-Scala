package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.exceptions.MalformedVolumeException
import org.hathitrust.htrc.textprocessing.runningheaders.{Page, PageStructureParser}
import org.hathitrust.htrc.tools.scala.xml.XmlReader
import org.w3c.dom.{Document, Element, NodeList}

import java.nio.charset.CodingErrorAction
import java.util.zip.ZipFile
import javax.xml.namespace.NamespaceContext
import javax.xml.xpath.{XPathConstants, XPathFactory}
import scala.io.{Codec, Source}
import scala.util.{Try, Using}

object HtrcVolume {
  protected object MetsXmlReader extends XmlReader {
    override protected def isXmlReaderNamespaceAware: Boolean = true
  }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def from(pairtreeVolume: PairtreeVolume)
          (implicit codec: Codec): Try[HtrcVolume] =
    Using.Manager { use =>
      val metsReader = use(Source.fromFile(pairtreeVolume.metsPath).bufferedReader())
      val volZip = use(new ZipFile(pairtreeVolume.zipPath, codec.charSet))

      val volumeId = pairtreeVolume.volumeId
      val metsXml = MetsXmlReader.readXml(metsReader).get

      // don't fail due to decoding errors
      codec.onMalformedInput(CodingErrorAction.REPLACE)
      codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

      // extract the correct sequence of page filenames from the METS file and
      // attempt to map these page filenames to ZIP entries
      val pageZipEntries = getPageSeqMapping(metsXml).map {
        case (seq, f) => (seq, f, volZip.getEntry(s"${volumeId.partsClean._2}/$f"))
      }

      // check for inconsistencies between the METS page sequence and ZIP contents
      // and throw exception if any are found
      val missingPages = pageZipEntries.withFilter(_._3 == null).map(_._2)
      if (missingPages.nonEmpty)
        throw MalformedVolumeException(s"[${volumeId.uncleanId}] " +
          s"Missing page entries in volume ZIP file: " + missingPages.mkString(", "))

      val pages = pageZipEntries.view.map { case (seq, _, zipEntry) =>
        Using.resource(volZip.getInputStream(zipEntry))(new HtrcPage(seq, _))
      }.toIndexedSeq

      new HtrcVolume(volumeId, pages)
    }

  /**
    * Retrieve the correct page sequence from METS
    *
    * @param metsXml The METS XML
    * @return The sequence of page file names
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Null"))
  protected def getPageSeqMapping(metsXml: Document): Seq[(String, String)] = {
    val xpath = XPathFactory.newInstance().newXPath()
    xpath.setNamespaceContext(new NamespaceContext {
      override def getPrefixes(namespaceURI: String): java.util.Iterator[String] = null

      override def getPrefix(namespaceURI: String): String = null

      override def getNamespaceURI(prefix: String): String = prefix match {
        case "METS" => "http://www.loc.gov/METS/"
        case "xlink" => "http://www.w3.org/1999/xlink"
        case _ => null
      }
    })

    val metsOcrTxtFiles = xpath.evaluate(
      """//METS:fileGrp[@USE="ocr"]/METS:file[@MIMETYPE="text/plain"]/METS:FLocat""",
      metsXml, XPathConstants.NODESET
    ).asInstanceOf[NodeList]

    for {
      i <- 0 until metsOcrTxtFiles.getLength
      metsTxt = metsOcrTxtFiles.item(i).asInstanceOf[Element]
      pageFileName = metsTxt.getAttribute("xlink:href")
      pageSeq = metsTxt.getParentNode.asInstanceOf[Element].getAttribute("SEQ")
    } yield pageSeq -> pageFileName
  }

  private val htrcStructuredPageBuilder: (HtrcPage, Int, Int) => HtrcStructuredPage =
    (page, headerLineCount, footerLineCount) =>
      new HtrcStructuredPage(page.seq, page.textLines, headerLineCount, footerLineCount)
}

class HtrcVolume(val volumeId: HtrcVolumeId, val pages: IndexedSeq[HtrcPage]) extends Serializable {
  import HtrcVolume.htrcStructuredPageBuilder

  lazy val structuredPages: IndexedSeq[HtrcStructuredPage] =
    PageStructureParser.parsePageStructure(pages, builder = htrcStructuredPageBuilder)

  def text: String = pages.view.map(_.asInstanceOf[Page].text).mkString(System.lineSeparator())

  override def hashCode(): Int = volumeId.hashCode()

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: HtrcVolume => volumeId == other.volumeId
    case _ => false
  }

  override def toString: String = f"HtrcVolume($volumeId, ${pages.length}%,d pages)"
}
