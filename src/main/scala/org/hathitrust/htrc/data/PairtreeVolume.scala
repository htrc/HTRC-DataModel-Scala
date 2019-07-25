package org.hathitrust.htrc.data

import java.io.File

import gov.loc.repository.pairtree.Pairtree
import org.hathitrust.htrc.data.exceptions.InvalidPairtreePathException

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

object PairtreeVolume {
  protected val pairtreeFilePartsRegex: Regex = """^(.*?)([^/]+)/pairtree_root/.+/([^/]+)/[^/]+$""".r
  protected val pairtree: Pairtree = new Pairtree()

  /**
    * Parses an HTRC pairtree file path into a `PairtreeVolume` that can be used
    * to extract metadata about the document
    *
    * @param filePath The pairtree file path
    * @return The `Try[PairtreeVolume]` containing the success or failure
    */
  def from(filePath: String): Try[PairtreeVolume] = normalize(filePath) match {
    case pairtreeFilePartsRegex(pairtreeRoot, libId, cleanIdPart) =>
      val uncleanIdPart = pairtree.uncleanId(cleanIdPart)
      val uncleanId = s"$libId.$uncleanIdPart"
      Success(PairtreeVolume(HtrcVolumeId(uncleanId), pairtreeRoot))

    case _ => Failure(InvalidPairtreePathException(filePath))
  }

  /**
    * Parses an HTRC pairtree file into a `PairtreeVolume` that can be used
    * to extract metadata about the document
    *
    * @param file The pairtree file
    * @return The `Try[PairtreeVolume]` containing the success or failure
    */
  def from(file: File): Try[PairtreeVolume] = Try(file.getCanonicalPath).flatMap(from)

  /**
    * Creates a `PairtreeVolume` instance from a HTRC volume identifier
    *
    * @param volumeId The volume identifier
    * @return The `PairtreeVolume` instance
    */
  def apply(volumeId: HtrcVolumeId): PairtreeVolume = PairtreeVolume(volumeId, "")

  /**
    * Creates a `PairtreeVolume` instance from a HTRC volume identifier and pairtree root path
    *
    * @param volumeId The volume identifier
    * @param pairtreeRoot The pairtree root path
    * @return The `PairtreeVolume` instance
    */
  def apply(volumeId: HtrcVolumeId, pairtreeRoot: String): PairtreeVolume = {
    val root = Some(pairtreeRoot.trim)
      .filterNot(_.isEmpty)
      .map(s => if (s.endsWith("/")) s else s + "/")
      .getOrElse("")

    new PairtreeVolume(volumeId, root)
  }

  def unapply(vol: PairtreeVolume): Option[(HtrcVolumeId, String)] =
    Some((vol.volumeId, vol.pairtreeRoot))

  protected def normalize(path: String): String = path.replaceAll("/{2,}", "/")
}

class PairtreeVolume(val volumeId: HtrcVolumeId, val pairtreeRoot: String) extends Serializable {
  import PairtreeVolume._

  require(pairtreeRoot.isEmpty || pairtreeRoot.endsWith("/"))

  def this(volumeId: HtrcVolumeId) = this(volumeId, "")

  protected def ppath: String = pairtree.mapToPPath(volumeId.parts._2)

  /**
    * Returns the root folder for the document
    *
    * @return The document folder path
    */
  def rootPath: String = {
    val (libId, cleanIdPart) = volumeId.partsClean
    s"$pairtreeRoot$libId/pairtree_root/$ppath/$cleanIdPart"
  }

  /**
    * Returns the document path prefix for this pairtree document; for example,
    * a volume with ID mdp.39015063051745 would generate the document path prefix
    * mdp/pairtree_root/39/01/50/63/05/17/45/39015063051745/39015063051745
    * By appending ".zip" or ".mets.xml" to this path you can point to the pairtree volume ZIP
    * or the volume METS metadata file, as desired.
    *
    * @return The document path
    */
  def pathPrefix: String = {
    val (libId, cleanIdPart) = volumeId.partsClean
    s"$pairtreeRoot$libId/pairtree_root/$ppath/$cleanIdPart/$cleanIdPart"
  }

  /**
    * Convenience method for quickly getting the path to the ZIP file.
    *
    * @return The relative path to the ZIP file
    */
  def zipPath: String = s"$pathPrefix.zip"

  /**
    * Convenience method for quickly getting the path to the METS XML file.
    *
    * @return The relative path to the METS XML file
    */
  def metsPath: String = s"$pathPrefix.mets.xml"

  /**
    * Convenience method for quickly getting the path to the JSON metadata file.
    *
    * @return The relative path to the JSON metadata file
    */
  def jsonMetadataPath: String = s"$pathPrefix.json"

  /**
    * Convenience method for quickly getting the path to the EF file.
    *
    * @return The relative path to the EF file
    */
  def extractedFeaturesPath: String = {
    val (libId, cleanIdPart) = volumeId.partsClean
    s"$pairtreeRoot$libId/pairtree_root/$ppath/$cleanIdPart/$libId.$cleanIdPart.json.bz2"
  }

  override def hashCode(): Int = (volumeId.uncleanId + pairtreeRoot).hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: PairtreeVolume => volumeId == other.volumeId && pairtreeRoot == other.pairtreeRoot
    case _ => false
  }
}