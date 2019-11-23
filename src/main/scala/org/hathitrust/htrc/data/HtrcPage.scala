package org.hathitrust.htrc.data

import java.io.{File, InputStream}
import java.nio.file.Path
import java.util.Scanner

import org.hathitrust.htrc.data.ops.PageOps
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}
import org.hathitrust.htrc.tools.scala.io.IOUtils.readLinesWithDelimiters

import scala.io.Codec

class HtrcPage(val seq: String, val textLines: Lines) extends Page with PageOps with Serializable {

  def this(seq: String, lines: Iterator[String]) = this(seq, lines.toIndexedSeq)

  protected def this(seq: String, scanner: Scanner) = this(seq, readLinesWithDelimiters(scanner))

  def this(seq: String, text: String) = this(seq, new Scanner(text))

  def this(seq: String, file: File)
          (implicit codec: Codec) = this(seq, new Scanner(file, codec.name))

  def this(seq: String, path: Path)
          (implicit codec: Codec) = this(seq, new Scanner(path, codec.name))

  def this(seq: String, stream: InputStream)
          (implicit codec: Codec) = this(seq, new Scanner(stream, codec.name))

  override def hashCode(): Int = seq.hashCode + textLines.length

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: HtrcPage => seq == other.seq && textLines == other.textLines
    case _ => false
  }

  override def toString: String = f"HtrcPage($seq, ${textLines.length}%,d lines)"

}
