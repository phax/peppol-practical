/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.helger.commons.charset.CCharset;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.compare.ComparatorStringLongestFirst;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;

public final class MainRestoreOldContent
{
  private static final String BASE_DIR = "src/main/resources/viewpages/en/";

  public static void main (final String [] args)
  {
    final String sFile1 = "en_gb.properties";
    final String sFile2 = "ws.xml1";

    // Don't change
    final File f1 = new File (BASE_DIR, sFile1);
    final Map <String, String> k = new TreeMap <String, String> (new ComparatorStringLongestFirst ());
    for (String s : SimpleFileIO.readFileLines (f1, CCharset.CHARSET_UTF_8_OBJ))
      if (!s.startsWith ("#") && s.length () > 0)
      {
        final int i = s.indexOf (']');
        s = s.substring (i + 1);
        final String [] x = RegExHelper.getAllMatchingGroupValues ("(.+)\\s+=\\s+\"(.+)\"", s);
        k.put (x[0], x[1]);
      }

    final File f2 = new File (BASE_DIR, sFile2);
    String s2 = SimpleFileIO.readFileAsString (f2, CCharset.CHARSET_UTF_8_OBJ);
    s2 = RegExHelper.stringReplacePattern ("\\s*<text strid=\"(.+)\" />\\s*", s2, "$1");
    s2 = s2.replace (" externalhref=", " href=");

    final String [] st = ArrayHelper.newArray (k.keySet (), String.class);
    final String [] rt = ArrayHelper.newArray (k.values (), String.class);
    final String s21 = StringHelper.replaceMultiple (s2, st, rt);
    System.out.println (s21);
  }
}
