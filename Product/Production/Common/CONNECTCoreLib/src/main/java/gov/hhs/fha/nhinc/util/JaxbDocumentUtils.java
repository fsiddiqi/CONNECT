/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.util;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

/**
 * Utility methods for working with the JAXB representation of documents.
 */
public final class JaxbDocumentUtils {

    private JaxbDocumentUtils() {
    }

    /**
     * Null safe pull of the 1st slot value, if a slot of the expectedType exists.
     *
     * @param slotList list of candidate slots
     * @param expectedType type of slot to look for
     * @return the 1st value in the 1st matching slot, if found
     */
    public static Optional<String> findSlotType(List<SlotType1> slotList, final String expectedType) {
        Predicate<SlotType1> slotPredicate = new Predicate<SlotType1>() {
            @Override
            public boolean apply(SlotType1 slot) {
                return ((slot != null) && expectedType.equals(slot.getName()) && slot.getValueList() != null
                    && !slot.getValueList().getValue().isEmpty());
            }
        };
        Optional<SlotType1> slot = Iterables.tryFind(slotList, slotPredicate);

        if (!slot.isPresent()) {
            return Optional.absent();
        }

        return Optional.of(slot.get().getValueList().getValue().get(0));
    }
}
