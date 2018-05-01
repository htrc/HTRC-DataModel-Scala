package org.hathitrust.htrc.data

import gov.loc.repository.pairtree.Pairtree
import org.hathitrust.htrc.data.exceptions.InvalidHtrcVolumeIdException

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object HtrcVolumeId {
  protected val idRegex: Regex = """([^\.]+)\.(.+)""".r
  protected val pairtree: Pairtree = new Pairtree()

  def parseUnclean(uncleanId: String): Try[HtrcVolumeId] = uncleanId.find(_ == '.') match {
    case Some(_) => Success(HtrcVolumeId(uncleanId))
    case None => Failure(InvalidHtrcVolumeIdException(uncleanId))
  }

  def parseClean(cleanId: String): Try[HtrcVolumeId] = cleanId match {
    case idRegex(libId, cleanIdPart) =>
      val uncleanIdPart = pairtree.uncleanId(cleanIdPart)
      val uncleanId = s"$libId.$uncleanIdPart"
      Success(HtrcVolumeId(uncleanId))

    case _ => Failure(InvalidHtrcVolumeIdException(cleanId))
  }
}

case class HtrcVolumeId(uncleanId: String) {
  import HtrcVolumeId._

  require(uncleanId.contains("."), s"Invalid HTRC volume id: $uncleanId")

  def cleanId: String = {
    val (libId, cleanIdPart) = partsClean
    s"$libId.$cleanIdPart"
  }

  def libId: String = parts._1

  def toPairtreeDoc: PairtreeVolume = PairtreeVolume(this)

  def toPairtreeDoc(pairtreeRoot: String): PairtreeVolume = PairtreeVolume(this, pairtreeRoot)

  def parts: (String, String) = uncleanId match {
    case idRegex(libId, uncleanIdPart) => libId -> uncleanIdPart
  }

  def partsClean: (String, String) = {
    val (libId, uncleanIdPart) = parts
    libId -> pairtree.cleanId(uncleanIdPart)
  }

  override def toString: String = uncleanId
}