package org.hathitrust.htrc.data

import java.io.File

import gov.loc.repository.pairtree.Pairtree
import org.hathitrust.htrc.data.exceptions.InvalidPathException
import org.hathitrust.htrc.tools.scala.implicits.StringsImplicits._

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object StubbytreeVolume {
  protected val efStubbytreeFilePartsRegex: Regex = """^(.*?)([^/]+)/[^/]+/([^/.]+)\.([^/]+)\.json\.bz2$""".r
  protected val stubbytreeFilePartsRegex: Regex = """^(.*?)([^/]+)/[^/]+/([^/]+)\.[^.]+$""".r
  protected val pairtree: Pairtree = new Pairtree()

  /**
    * Parses an HTRC stubbytree file path into a `StubbytreeVolume` that can be used
    * to extract metadata about the document
    *
    * @param filePath The stubbytree file path
    * @return The `Try[StubbytreeVolume]` containing the success or failure
    */
  def from(filePath: String): Try[StubbytreeVolume] = normalize(filePath) match {
    case efStubbytreeFilePartsRegex(root, libId, libIdCheck, cleanIdPart) if libId equals libIdCheck =>
      val uncleanIdPart = pairtree.uncleanId(cleanIdPart)
      val uncleanId = s"$libId.$uncleanIdPart"
      Success(StubbytreeVolume(HtrcVolumeId(uncleanId), root))

    case stubbytreeFilePartsRegex(root, libId, cleanIdPart) =>
      val uncleanIdPart = pairtree.uncleanId(cleanIdPart)
      val uncleanId = s"$libId.$uncleanIdPart"
      Success(StubbytreeVolume(HtrcVolumeId(uncleanId), root))

    case _ => Failure(InvalidPathException(filePath))
  }

  /**
    * Parses an HTRC stubbytree file into a `StubbytreeVolume` that can be used
    * to extract metadata about the document
    *
    * @param file The stubbytree file
    * @return The `Try[StubbytreeVolume]` containing the success or failure
    */
  def from(file: File): Try[StubbytreeVolume] = Try(file.getCanonicalPath).flatMap(from)

  def from(pairtreeVol: PairtreeVolume, newRoot: String = ""): StubbytreeVolume =
    StubbytreeVolume(pairtreeVol.volumeId, newRoot)

  /**
    * Creates a `StubbytreeVolume` instance from a HTRC volume identifier
    *
    * @param volumeId The volume identifier
    * @return The `StubbytreeVolume` instance
    */
  def apply(volumeId: HtrcVolumeId): StubbytreeVolume = StubbytreeVolume(volumeId, "")

  /**
    * Creates a `StubbytreeVolume` instance from a HTRC volume identifier and stubbytree root path
    *
    * @param volumeId       The volume identifier
    * @param stubbytreeRoot The stubbytree root path
    * @return The `StubbytreeVolume` instance
    */
  def apply(volumeId: HtrcVolumeId, stubbytreeRoot: String): StubbytreeVolume = {
    val root = Some(stubbytreeRoot.trim)
      .filterNot(_.isEmpty)
      .map(s => if (s.endsWith("/")) s else s + "/")
      .getOrElse("")

    new StubbytreeVolume(volumeId, root)
  }

  def unapply(vol: StubbytreeVolume): Option[(HtrcVolumeId, String)] =
    Some((vol.volumeId, vol.stubbytreeRoot))

  protected def normalize(path: String): String = path.replaceAll("/{2,}", "/")
}

class StubbytreeVolume(val volumeId: HtrcVolumeId, val stubbytreeRoot: String) extends Serializable {

  require(stubbytreeRoot.isEmpty || stubbytreeRoot.endsWith("/"))

  def this(volumeId: HtrcVolumeId) = this(volumeId, "")

  /**
    * Returns the root folder for the document
    *
    * @return The document folder path
    */
  def rootPath: String = {
    val (libId, cleanIdPart) = volumeId.partsClean
    val stubbyPart = cleanIdPart.takeEvery(3)
    s"$stubbytreeRoot$libId/$stubbyPart"
  }

  /**
    * Returns the document path prefix for this stubbytree document; for example,
    * a volume with ID mdp.39015063051745 would generate the document path prefix
    * mdp/31654/39015063051745
    * By appending ".zip" or ".mets.xml" to this path you can point to the stubbytree volume ZIP
    * or the volume METS metadata file, as desired.
    *
    * @return The document path
    */
  def pathPrefix: String = {
    val (_, cleanIdPart) = volumeId.partsClean
    s"$rootPath/$cleanIdPart"
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
    s"$rootPath/$libId.$cleanIdPart.json.bz2"
  }

  override def hashCode(): Int = (volumeId.uncleanId + stubbytreeRoot).hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: StubbytreeVolume => volumeId == other.volumeId && stubbytreeRoot == other.stubbytreeRoot
    case _ => false
  }
}