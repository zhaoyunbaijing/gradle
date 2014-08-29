/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.model.internal.report.unbound;

import com.google.common.collect.Lists;

import java.util.List;

public class UnboundRule {

    private final String descriptor;

    private final List<UnboundRuleInput> immutableInputs;
    private final List<UnboundRuleInput> mutableInputs;

    public UnboundRule(String descriptor, List<UnboundRuleInput> immutableInputs, List<UnboundRuleInput> mutableInputs) {
        this.descriptor = descriptor;
        this.immutableInputs = immutableInputs;
        this.mutableInputs = mutableInputs;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public List<UnboundRuleInput> getImmutableInputs() {
        return immutableInputs;
    }

    public List<UnboundRuleInput> getMutableInputs() {
        return mutableInputs;
    }

    public static Builder descriptor(String descriptor) {
        return new Builder(descriptor);
    }

    public static class Builder {

        private String descriptor;
        private final List<UnboundRuleInput> immutableInputs = Lists.newArrayList();
        private final List<UnboundRuleInput> mutableInputs = Lists.newArrayList();

        public Builder(String descriptor) {
            this.descriptor = descriptor;
        }

        public Builder immutableInput(UnboundRuleInput.Builder inputBuilder) {
            immutableInputs.add(inputBuilder.build());
            return this;
        }

        public Builder mutableInput(UnboundRuleInput.Builder inputBuilder) {
            mutableInputs.add(inputBuilder.build());
            return this;
        }

        public UnboundRule build() {
            return new UnboundRule(descriptor, immutableInputs, mutableInputs);
        }
    }
}
