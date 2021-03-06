package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import org.junit.Test;

public class MediaWikiTest {

  @Test
  public void testEncode_empty() {
    // GIVEN
    String in = "";

    // WHEN
    String encoded = MediaWiki.urlEncode(in);
    String decoded = MediaWiki.urlDecode(encoded);

    // THEN
    assertEquals(in, decoded);
    assertEquals("", encoded);
  }

  @Test
  public void testEncode_specialChars() {
    // GIVEN
    String in = "a+&?=;.-";

    // WHEN
    String encoded = MediaWiki.urlEncode(in);
    String decoded = MediaWiki.urlDecode(encoded);

    // THEN
    assertEquals(in, decoded);
    assertEquals("a%2B%26%3F%3D%3B.-", encoded);
  }

  @Test
  public void testHtmlUnescape_empty() {
    // GIVEN
    String in = "";

    // WHEN
    String unescaped = MediaWiki.htmlUnescape(in);

    // THEN
    assertEquals("", unescaped);
  }

  @Test
  public void testHtmlUnescape_amp() {
    // GIVEN
    String in = "&amp;";

    // WHEN
    String unescaped = MediaWiki.htmlUnescape(in);

    // THEN
    assertEquals("&", unescaped);
  }

  @Test
  public void testHtmlUnescape_quotes() {
    // GIVEN
    String in = "&quot;t&quot;&nbsp;'&gt;&lt;'";

    // WHEN
    String unescaped = MediaWiki.htmlUnescape(in);

    // THEN
    assertEquals("\"t\"\u00A0'><'", unescaped);
  }

  @Test
  public void testValuesStable() {

    // GIVEN / WHEN
    ImmutableList<MediaWiki.Version> versions = MediaWiki.Version.valuesStable();

    // THEN
    ImmutableList<MediaWiki.Version> expected = ImmutableList.<MediaWiki.Version>builder() //
        .add(MediaWiki.Version.MW1_19) //
        .add(MediaWiki.Version.MW1_23) //
        .add(MediaWiki.Version.MW1_24) //
        .build();

    GAssert.assertEquals(expected, versions);

    ImmutableList<MediaWiki.Version> unstable = ImmutableList.<MediaWiki.Version>builder() //
        .add(MediaWiki.Version.MW1_14) //
        .add(MediaWiki.Version.MW1_15) //
        .add(MediaWiki.Version.MW1_16) //
        .add(MediaWiki.Version.MW1_17) //
        .add(MediaWiki.Version.MW1_18) //
        .add(MediaWiki.Version.MW1_20) //
        .add(MediaWiki.Version.MW1_21) //
        .add(MediaWiki.Version.MW1_22) //
            // --
        .add(MediaWiki.Version.MW1_25) //
        .build();

    assertFalse("there should be unstable versions as reference test", unstable.isEmpty());
    Sets.SetView<MediaWiki.Version> intersection = Sets //
        .intersection(ImmutableSet.copyOf(versions), ImmutableSet.copyOf(unstable));
    assertTrue("no unstable versions should be found:\n" + intersection, intersection.isEmpty());

    ImmutableSet<MediaWiki.Version> allVersions = ImmutableSet.copyOf(MediaWiki.Version.values());
    ImmutableSet<MediaWiki.Version> stableAndUnstableVersions = //
        ImmutableSet.<MediaWiki.Version>builder() //
            .addAll(unstable) //
            .addAll(expected) //
            .add(MediaWiki.Version.DEVELOPMENT) //
            .add(MediaWiki.Version.UNKNOWN) //
            .build();
    GAssert.assertEquals(allVersions, stableAndUnstableVersions);
  }

  @Test
  public void testGetLatest() {
    // GIVEN /  WHEN
    MediaWiki.Version latest = MediaWiki.Version.getLatest();

    // THEN
    assertEquals(MediaWiki.Version.MW1_24, latest);
  }

  @Test
  public void testIsStableVersion_latest() {
    assertTrue(MediaWiki.Version.isStableVersion(MediaWiki.Version.getLatest()));
  }

  @Test
  public void testIsStableVersion_unknown_fail() {
    assertFalse(MediaWiki.Version.isStableVersion(MediaWiki.Version.UNKNOWN));
  }

  @Test
  public void testIsStableVersion_null() {
    assertFalse(MediaWiki.Version.isStableVersion(null));
  }

  @Test
  public void testIsStableVersion_development_fail() {
    assertFalse(MediaWiki.Version.isStableVersion(MediaWiki.Version.DEVELOPMENT));
  }

  @Test
  public void testGreaterEqThen_development() {
    assertFalse(MediaWiki.Version.MW1_22.greaterEqThen(MediaWiki.Version.DEVELOPMENT));
    assertTrue(MediaWiki.Version.DEVELOPMENT.greaterEqThen(MediaWiki.Version.MW1_22));
  }

  @Test
  public void testGreaterEqThen_unknown() {
    assertTrue(MediaWiki.Version.MW1_22.greaterEqThen(MediaWiki.Version.UNKNOWN));
    assertFalse(MediaWiki.Version.UNKNOWN.greaterEqThen(MediaWiki.Version.MW1_22));
  }

  @Test
  public void testGreaterEqThen_unknown_development() {
    assertTrue(MediaWiki.Version.DEVELOPMENT.greaterEqThen(MediaWiki.Version.UNKNOWN));
    assertFalse(MediaWiki.Version.UNKNOWN.greaterEqThen(MediaWiki.Version.DEVELOPMENT));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUrlDecodeUnchecked() {
    MediaWiki.urlDecodeUnchecked("&", "");
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUrlEncodeUnchecked() {
    MediaWiki.urlEncodeUnchecked("&", "INVALID");
    fail();
  }

  @Test
  public void testGetCharset() {
    assertEquals("UTF-8", MediaWiki.getCharset());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetFieldUnchecked() {
    MediaWiki.getFieldUnchecked(Object.class, "INVALID");
    fail();
  }

  @Test
  public void testNamespaces_old() {
    // GIVEN / WHEN
    String nsString = MWAction.createNsString(MWAction.nullSafeCopyOf(MediaWiki.NS_ALL));

    // THEN
    assertEquals("0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15", nsString);
  }

  @Test
  public void testNamespaces() {
    // GIVEN / WHEN
    String nsString = MWAction.createNsString(MediaWiki.NS_EVERY);

    // THEN
    assertEquals("0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15", nsString);
  }

}
