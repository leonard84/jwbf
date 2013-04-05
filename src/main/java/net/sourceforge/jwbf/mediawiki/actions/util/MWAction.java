/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
package net.sourceforge.jwbf.mediawiki.actions.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * @author Thomas Stock
 * 
 */
@Slf4j
public abstract class MWAction implements ContentProcessable {

  private Version[] v;
  private boolean hasMore = true;

  /**
   * 
   * @return true if and changes state to false
   */
  public boolean hasMoreMessages() {
    final boolean b = hasMore;
    hasMore = false;
    return b;
  }

  /**
   * 
   * @param b
   *          if so
   */
  public void setHasMoreMessages(boolean b) {
    hasMore = b;
  }

  /**
   * 
   * @deprecated use {@link #MWAction(Version)} instead
   */
  @Deprecated
  protected MWAction() {

  }

  /**
   * 
   * @param v
   *          of the bot
   * 
   *          if action is incompatible
   */
  protected MWAction(Version v) {
    checkVersionNewerEquals(v);

  }

  /**
   * Deals with the MediaWiki API's response by parsing the provided text.
   * 
   * @param s
   *          the answer to the most recently generated MediaWiki API request
   * @param hm
   *          the requestor message
   * @return the returning text
   * 
   *         on processing problems
   * 
   */
  public String processReturningText(final String s, final HttpAction hm) {
    return processAllReturningText(s);
  }

  /**
   * @param s
   *          the returning text
   * @return the returning text
   * 
   *         never
   * 
   */
  public String processAllReturningText(final String s) {
    return s;
  }

  /**
   * 
   * @return a
   */
  private Version[] getVersionArray() {

    if (v != null) {
      return v;
    }
    v = findSupportedVersions(getClass());
    return v;
  }

  public static final Version[] findSupportedVersions(Class<?> clazz) {
    if (clazz.getName().contains(Object.class.getName())) {
      Version[] v = new MediaWiki.Version[1];
      v[0] = Version.UNKNOWN;
      return v;
    } else if (clazz.isAnnotationPresent(SupportedBy.class)) {
      SupportedBy sb = clazz.getAnnotation(SupportedBy.class);
      if (log.isDebugEnabled()) {
        Version[] vtemp = sb.value();
        StringBuffer sv = new StringBuffer();
        for (int i = 0; i < vtemp.length; i++) {
          sv.append(vtemp[i].getNumber() + ", ");
        }
        String svr = sv.toString().trim();
        svr = svr.substring(0, svr.length() - 1);

        log.debug("found support for: " + svr + " in ↲ \n\t class " + clazz.getCanonicalName());

      }
      return sb.value();
    } else {
      return findSupportedVersions(clazz.getSuperclass());
    }
  }

  protected void checkVersionNewerEquals(Version v) {
    if (getSupportedVersions().contains(v)) {
      return;
    }
    for (Version vx : getSupportedVersions()) {
      if (v.greaterEqThen(vx)) {
        return;
      }
    }
    throw new VersionException("unsupported version: " + v);
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Version> getSupportedVersions() {
    Collection<Version> v = new ArrayList<Version>();

    Version[] va = getVersionArray();
    for (int i = 0; i < va.length; i++) {
      v.add(va[i]);
    }

    return v;
  }

  /**
   * helper method generating a namespace string as required by the MW-api.
   * 
   * @param namespaces
   *          namespace as
   * @return with numbers seperated by |
   */
  public static String createNsString(int... namespaces) {

    StringBuffer namespaceString = new StringBuffer();
    String result = "";
    if (namespaces != null && namespaces.length != 0) {
      for (int nsNumber : namespaces) {
        namespaceString.append(nsNumber + "|");
      }
      result = namespaceString.toString();
      // remove last '|'
      if (result.endsWith("|")) {
        result = result.substring(0, result.length() - 1);
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated see interface
   */
  @Deprecated
  public boolean isSelfExecuter() {
    return false;
  }

  protected Element getRootElement(final String xml) {
    SAXBuilder builder = new SAXBuilder();
    Element root = null;
    try {
      Reader i = new StringReader(xml);
      Document doc = builder.build(new InputSource(i));

      root = doc.getRootElement();

    } catch (JDOMException e) {
      log.error(xml);
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    return root;
  }
}