/*
 * Copyright 2016 Marco Ammon <ammon.marco@t-online.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

/**
 *
 * @author Marco Ammon <ammon.marco@t-online.de>
 */


import static org.junit.Assert.assertEquals;

import java.util.List;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class UnreviewedPagesTitlesIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = ApiMatcherBuilder.of() //
      .param("action", "query") //
      .param("format", "xml") //
      .param("list", "unreviewedpages") //
      .param("urlimit", "50") //
      .param("urnamespace", "0") //
      .build();

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("unreviewedpages.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    UnreviewedPagesTitles testee = new UnreviewedPagesTitles(bot, MediaWiki.NS_MAIN);
    List<String> resultList = testee.getCopyOf(15); // query-continue is not implemented

    // THEN
    ImmutableList<String> expected = ImmutableList
        .of("1-1-1", "2014-15 Season 1 eGamers Starcraft II Open/Participants",
            "2014 WCS Season 1 Korea GSL", "4 Warpgate All In (vs. Protoss)");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

  @Test
  public void testOne() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("unreviewedpages.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    UnreviewedPagesTitles testee = new UnreviewedPagesTitles(bot, MediaWiki.NS_MAIN);
    List<String> resultList = testee.getCopyOf(1);

    // THEN
    ImmutableList<String> expected = ImmutableList.of("1-1-1");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

}
