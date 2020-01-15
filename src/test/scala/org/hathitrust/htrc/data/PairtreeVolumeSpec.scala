package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.exceptions.InvalidPairtreePathException
import org.scalatest.ParallelTestExecution
import org.scalatest.TryValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Failure

class PairtreeVolumeSpec extends AnyFlatSpec
  with Matchers with ParallelTestExecution {

  "A PairtreeVolume" should "be successfully created from a valid full path" in {
    val volPath = "/NGPD/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
    val volume = PairtreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "/NGPD/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "/NGPD/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f"

    val volumeId = volume.volumeId

    volumeId.libId shouldBe "uc2"
    volumeId.cleanId shouldBe "uc2.ark+=13960=t4qj7970f"
    volumeId.partsClean._2 shouldBe "ark+=13960=t4qj7970f"
    volumeId.uncleanId shouldBe "uc2.ark:/13960/t4qj7970f"
    volumeId.parts._2 shouldBe "ark:/13960/t4qj7970f"
  }

  it should "be successfully created from a valid root path" in {
    val volPath = "/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
    val volume = PairtreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f"

    val volumeId = volume.volumeId

    volumeId.libId shouldBe "uc2"
    volumeId.cleanId shouldBe "uc2.ark+=13960=t4qj7970f"
    volumeId.partsClean._2 shouldBe "ark+=13960=t4qj7970f"
    volumeId.uncleanId shouldBe "uc2.ark:/13960/t4qj7970f"
    volumeId.parts._2 shouldBe "ark:/13960/t4qj7970f"
  }

  it should "be successfully created from a valid relative path" in {
    val volPath = "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
    val volume = PairtreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f"

    val volumeId = volume.volumeId

    volumeId.libId shouldBe "uc2"
    volumeId.cleanId shouldBe "uc2.ark+=13960=t4qj7970f"
    volumeId.partsClean._2 shouldBe "ark+=13960=t4qj7970f"
    volumeId.uncleanId shouldBe "uc2.ark:/13960/t4qj7970f"
    volumeId.parts._2 shouldBe "ark:/13960/t4qj7970f"
  }

  it should "fail to be created from an invalid path" in {
    val wrongPath = "/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
    val volume = PairtreeVolume.from(wrongPath)

    volume.failure shouldBe Failure(InvalidPairtreePathException(wrongPath))
  }

  it should "be convertible to a path prefix" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val pathPrefix = HtrcVolumeId.parseClean(cleanId).map(_.toPairtreeDoc.pathPrefix).success.value

    pathPrefix shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f"
  }

  it should "be convertible to a root path" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val rootPath = HtrcVolumeId.parseClean(cleanId).map(_.toPairtreeDoc.rootPath).success.value

    rootPath shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f"
  }

  it should "be convertible to a full root path" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val pairtreeRoot = "/pairtree/root"
    val fullPath = HtrcVolumeId.parseClean(cleanId).map(_.toPairtreeDoc(pairtreeRoot).rootPath).success.value

    fullPath shouldBe "/pairtree/root/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f"
  }

  it should "return the correct relative ZIP path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val zipPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toPairtreeDoc.zipPath).success.value

    zipPath shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
  }

  it should "return the correct full ZIP path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val pairtreeRoot = "/pairtree/root"
    val fullZipPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toPairtreeDoc(pairtreeRoot).zipPath).success.value

    fullZipPath shouldBe "/pairtree/root/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
  }

  it should "return the correct relative METS path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val metsPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toPairtreeDoc.metsPath).success.value

    metsPath shouldBe "uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.mets.xml"
  }

  it should "return the correct full METS path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val pairtreeRoot = "/pairtree/root"
    val fullMetsPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toPairtreeDoc(pairtreeRoot).metsPath).success.value

    fullMetsPath shouldBe "/pairtree/root/uc2/pairtree_root/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.mets.xml"
  }
}
