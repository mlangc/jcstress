/*
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jcstress.tests.atomics.references;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.concurrent.atomic.AtomicReference;

@JCStressTest
@Description("Shows why opaque mode must not used to publish objects with non final fields")
@Outcome(id = "42", expect = Expect.ACCEPTABLE)
@Outcome(id = "0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "observed partially constructed object")
@State
public class AtomicReferenceSetGetOpaqueTest {
    static class Holder {
        int x = 42;
    }

    final AtomicReference<Holder> ref = new AtomicReference<>();

    @Actor
    public void actor1() {
        ref.setOpaque(new Holder());
    }

    @Actor
    public void actor2(I_Result r) {
        while (true) {
            Holder h = ref.getOpaque();
            if (h == null) {
                Thread.onSpinWait();
                continue;
            }

            r.r1 = h.x;
            return;
        }
    }
}
