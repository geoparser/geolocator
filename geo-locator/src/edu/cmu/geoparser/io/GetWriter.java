/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import edu.cmu.geoparser.common.OSUtil;

public class GetWriter {

  public static IndexWriter getIndexWriter(String indexdirectory, double buffersize)
          throws IOException {
    Directory dir;
    if (OSUtil.isWindows())
      dir = FSDirectory.open(new File(indexdirectory));
    else
      dir = NIOFSDirectory.open(new File(indexdirectory));

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, analyzer);

    config.setOpenMode(OpenMode.CREATE_OR_APPEND);
    config.setRAMBufferSizeMB(buffersize);
    LogDocMergePolicy mergePolicy = new LogDocMergePolicy();
    mergePolicy.setMergeFactor(3);
    config.setMergePolicy(mergePolicy);

    IndexWriter writer = new IndexWriter(dir, config);
    return writer;
  }

  public static BufferedWriter getFileWriter(String filename) throws IOException {

    FileOutputStream fos = new FileOutputStream(filename, false);// rewrite the file of the same
                                                                 // name
    OutputStreamWriter ofw = new OutputStreamWriter(fos, "utf-8");
    BufferedWriter bw = new BufferedWriter(ofw);

    return bw;
  }

}
