package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.exceptions.InvalidHtrcVolumeIdException
import org.scalatest.ParallelTestExecution
import org.scalatest.TryValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class HtrcVolumeIdSpec extends AnyFlatSpec
  with Matchers with ParallelTestExecution {

  "An HtrcVolumeId" should "be successfully created from a valid (unclean) volume id" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    HtrcVolumeId.parseUnclean(uncleanId) shouldBe Success(HtrcVolumeId(`uncleanId`))
  }

  it should "be successfully created from a valid (clean) volume id" in {
    val cleanId = "loc.ark+=13960=t4qj7970f"
    HtrcVolumeId.parseClean(cleanId) shouldBe Success(HtrcVolumeId("loc.ark:/13960/t4qj7970f"))
  }

  it should "fail to be created from an invalid volume id" in {
    val invalidId = "ark:/13960/t4qj7970f"
    HtrcVolumeId.parseUnclean(invalidId) shouldBe Failure(InvalidHtrcVolumeIdException(`invalidId`))
  }

  it should "correctly return the component parts (lib, id)" in {
    val uncleanId = "uc2.ark:/13960/t4qj7970f"
    val volumeId = HtrcVolumeId.parseUnclean(uncleanId).success.value

    volumeId.parts shouldBe("uc2", "ark:/13960/t4qj7970f")
    volumeId.partsClean shouldBe("uc2", "ark+=13960=t4qj7970f")
    volumeId.libId shouldBe "uc2"
  }
}
