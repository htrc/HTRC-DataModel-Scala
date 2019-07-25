package org.hathitrust.htrc.data

import java.io.InputStream

import org.hathitrust.htrc.data.ops.PageOps
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}

import scala.io.{Codec, Source}

class HtrcPage(val seq: String, val textLines: Lines) extends Page with PageOps with Serializable {

  def this(seq: String, lines: Iterator[String]) = this(seq, lines.toIndexedSeq)

  def this(seq: String, source: Source) = this(seq, source.getLines())

  def this(seq: String, text: String) = this(seq, Source.fromString(text))

  def this(seq: String, stream: InputStream)
          (implicit codec: Codec) = this(seq, Source.fromInputStream(stream))

  override def hashCode(): Int = seq.hashCode + textLines.length

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: HtrcPage => seq == other.seq && textLines == other.textLines
    case _ => false
  }

  override def toString: String = f"HtrcPage($seq, ${textLines.length}%,d lines)"

}
