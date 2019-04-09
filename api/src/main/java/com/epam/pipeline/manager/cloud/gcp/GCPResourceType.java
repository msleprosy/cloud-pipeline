/*
 * Copyright 2017-2019 EPAM Systems, Inc. (https://www.epam.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.pipeline.manager.cloud.gcp;

import org.apache.commons.lang3.StringUtils;

public enum GCPResourceType {
    CPU {
        @Override
        public boolean isRequired(final GCPMachine machine) {
            return machine.getCpu() > 0;
        }

        @Override
        public String billingKey(final GCPBilling billing, final GCPMachine machine) {
            return String.format(BILLING_KEY_PATTERN, alias(), billing.alias(), machine.getFamily());
        }

        @Override
        public String family(final GCPMachine machine) {
            return machine.getFamily();
        }

        @Override
        public long price(final GCPMachine machine, final GCPResourcePrice price) {
            return machine.getCpu() * price.getNanos();
        }
    },

    RAM {
        @Override
        public boolean isRequired(final GCPMachine machine) {
            return machine.getRam() > 0;
        }

        @Override
        public String billingKey(final GCPBilling billing, final GCPMachine machine) {
            return String.format(BILLING_KEY_PATTERN, alias(), billing.alias(), machine.getFamily());
        }

        @Override
        public String family(final GCPMachine machine) {
            return machine.getFamily();
        }

        @Override
        public long price(final GCPMachine machine, final GCPResourcePrice price) {
            return Math.round(machine.getRam() * price.getNanos());
        }
    },

    GPU {
        @Override
        public boolean isRequired(final GCPMachine machine) {
            return machine.getGpu() > 0 && StringUtils.isNotBlank(machine.getGpuType());
        }

        @Override
        public String billingKey(final GCPBilling billing, final GCPMachine machine) {
            return String.format(BILLING_KEY_PATTERN, alias(), billing.alias(), machine.getGpuType().toLowerCase());
        }

        @Override
        public String family(final GCPMachine machine) {
            return machine.getGpuType();
        }

        @Override
        public long price(final GCPMachine machine, final GCPResourcePrice price) {
            return machine.getGpu() * price.getNanos();
        }
    };

    private static final String BILLING_KEY_PATTERN = "%s_%s_%s";

    public String alias() {
        return name().toLowerCase();
    }

    public abstract boolean isRequired(GCPMachine machine);

    public abstract String billingKey(GCPBilling billing, GCPMachine machine);

    public abstract String family(GCPMachine machine);

    public abstract long price(GCPMachine machine, GCPResourcePrice price);

}
