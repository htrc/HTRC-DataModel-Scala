package org.hathitrust.htrc

import scala.io.Codec
import scala.util.Try

package object data {

  /**
    * Creates an `HtrcVolume` from a volume id and pairtree root path
    *
    * @param id The volume id
    * @param pairtreeRootPath The pairtree root path
    * @param codec (implicit) The codec to use
    * @return A Try[HtrcVolume] containing the success or failure of the operation
    */
  def id2Volume(id: String, pairtreeRootPath: String)(implicit codec: Codec): Try[HtrcVolume] =
    HtrcVolumeId.parseUnclean(id).map(PairtreeVolume(_, pairtreeRootPath)).flatMap(HtrcVolume.from)

}
