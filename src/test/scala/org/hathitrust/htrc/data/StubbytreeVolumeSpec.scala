package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.exceptions.InvalidPathException
import org.scalatest.ParallelTestExecution
import org.scalatest.TryValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Failure

class StubbytreeVolumeSpec extends AnyFlatSpec
  with Matchers with ParallelTestExecution {

  "A StubbytreeVolume" should "be successfully created from a valid full path" in {
    val volPath = "/NGPD/uc2/a+30470/ark+=13960=t4qj7970f.zip"
    val volume = StubbytreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "/NGPD/uc2/a+30470/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "/NGPD/uc2/a+30470"

    volume.extractedFeaturesPath shouldBe "/NGPD/uc2/a+30470/uc2.ark+=13960=t4qj7970f.json.bz2"
  }

  it should "be successfully created from a valid root path" in {
    val volPath = "/uc2/a+30470/ark+=13960=t4qj7970f.zip"
    val volume = StubbytreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "/uc2/a+30470/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "/uc2/a+30470"
  }

  it should "be successfully created from a valid relative path" in {
    val volPath = "uc2/a+30470/ark+=13960=t4qj7970f.zip"
    val volume = StubbytreeVolume.from(volPath).success.value

    volume.pathPrefix shouldBe "uc2/a+30470/ark+=13960=t4qj7970f"
    volume.rootPath shouldBe "uc2/a+30470"
  }

  it should "fail to be created from an invalid path" in {
    val wrongPath = "/ar/k+/=1/39/60/=t/4q/j7/97/0f/ark+=13960=t4qj7970f/ark+=13960=t4qj7970f.zip"
    val volume = PairtreeVolume.from(wrongPath)

    volume.failure shouldBe Failure(InvalidPathException(wrongPath))
  }

  it should "be convertible to a path prefix" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val pathPrefix = HtrcVolumeId.parseClean(cleanId).map(_.toStubbytreeDoc.pathPrefix).success.value

    pathPrefix shouldBe "uc2/a+30470/ark+=13960=t4qj7970f"
  }

  it should "be convertible to a root path" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val rootPath = HtrcVolumeId.parseClean(cleanId).map(_.toStubbytreeDoc.rootPath).success.value

    rootPath shouldBe "uc2/a+30470"
  }

  it should "be convertible to a full root path" in {
    val cleanId = "uc2.ark+=13960=t4qj7970f"
    val stubbytreeRoot = "/stubbytree/root"
    val fullPath = HtrcVolumeId.parseClean(cleanId).map(_.toStubbytreeDoc(stubbytreeRoot).rootPath).success.value

    fullPath shouldBe "/stubbytree/root/uc2/a+30470"
  }

  it should "return the correct relative ZIP path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val zipPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toStubbytreeDoc.zipPath).success.value

    zipPath shouldBe "uc2/a+30470/ark+=13960=t4qj7970f.zip"
  }

  it should "return the correct full ZIP path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val stubbytreeRoot = "/stubbytree/root"
    val fullZipPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toStubbytreeDoc(stubbytreeRoot).zipPath).success.value

    fullZipPath shouldBe "/stubbytree/root/uc2/a+30470/ark+=13960=t4qj7970f.zip"
  }

  it should "return the correct relative METS path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val metsPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toStubbytreeDoc.metsPath).success.value

    metsPath shouldBe "uc2/a+30470/ark+=13960=t4qj7970f.mets.xml"
  }

  it should "return the correct full METS path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val stubbytreeRoot = "/stubbytree/root"
    val fullMetsPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toStubbytreeDoc(stubbytreeRoot).metsPath).success.value

    fullMetsPath shouldBe "/stubbytree/root/uc2/a+30470/ark+=13960=t4qj7970f.mets.xml"
  }

  it should "return the correct relative EF path" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val efPath = HtrcVolumeId.parseUnclean(uncleanId).map(_.toStubbytreeDoc.extractedFeaturesPath).success.value

    efPath shouldBe "uc2/a+30470/uc2.ark+=13960=t4qj7970f.json.bz2"
  }
}
