/*
 * Copyright 2016-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.rules.coercer;

import static org.junit.Assert.assertThat;

import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.BuildTargetFactory;
import com.facebook.buck.rules.BuildTargetSourcePath;
import com.facebook.buck.versions.FixedTargetNodeTranslator;
import com.facebook.buck.versions.TargetNodeTranslator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Optional;

public class SourceListTest {

  @Test
  public void translatedNamedSourcesTargets() {
    BuildTarget target = BuildTargetFactory.newInstance("//:rule");
    BuildTarget newTarget = BuildTargetFactory.newInstance("//something:else");
    TargetNodeTranslator translator =
        new FixedTargetNodeTranslator(ImmutableMap.of(target, newTarget));
    assertThat(
        translator.translate(
            SourceList.ofNamedSources(
                ImmutableSortedMap.of("name", new BuildTargetSourcePath(target)))),
        Matchers.equalTo(
            Optional.of(
                SourceList.ofNamedSources(
                    ImmutableSortedMap.of("name", new BuildTargetSourcePath(newTarget))))));
  }

  @Test
  public void untranslatedNamedSourcesTargets() {
    BuildTarget target = BuildTargetFactory.newInstance("//:rule");
    TargetNodeTranslator translator = new FixedTargetNodeTranslator(ImmutableMap.of());
    SourceList list =
        SourceList.ofNamedSources(
            ImmutableSortedMap.of("name", new BuildTargetSourcePath(target)));
    assertThat(
        translator.translate(list),
        Matchers.equalTo(Optional.empty()));
  }

  @Test
  public void translatedUnnamedSourcesTargets() {
    BuildTarget target = BuildTargetFactory.newInstance("//:rule");
    BuildTarget newTarget = BuildTargetFactory.newInstance("//something:else");
    TargetNodeTranslator translator =
        new FixedTargetNodeTranslator(ImmutableMap.of(target, newTarget));
    assertThat(
        translator.translate(
            SourceList.ofUnnamedSources(ImmutableSortedSet.of(new BuildTargetSourcePath(target)))),
        Matchers.equalTo(
            Optional.of(
                SourceList.ofUnnamedSources(
                    ImmutableSortedSet.of(new BuildTargetSourcePath(newTarget))))));
  }

  @Test
  public void untranslatedUnnamedSourcesTargets() {
    BuildTarget target = BuildTargetFactory.newInstance("//:rule");
    TargetNodeTranslator translator = new FixedTargetNodeTranslator(ImmutableMap.of());
    SourceList list =
        SourceList.ofUnnamedSources(ImmutableSortedSet.of(new BuildTargetSourcePath(target)));
    assertThat(
        translator.translate(list),
        Matchers.equalTo(Optional.empty()));
  }

}
