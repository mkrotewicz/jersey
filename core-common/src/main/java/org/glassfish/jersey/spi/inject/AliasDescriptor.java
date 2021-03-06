/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.jersey.spi.inject;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Injection binding description used to describe the aliases to main {@link Descriptor}.
 *
 * @author Petr Bouda (petr.bouda at oracle.com)
 */
public class AliasDescriptor {

    private final String contract;
    private final Set<Annotation> qualifiers = new LinkedHashSet<>();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<String> scope = Optional.empty();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private OptionalInt rank = OptionalInt.empty();

    /**
     * Creates a new alias.
     *
     * @param contract contract of the alias.
     */
     /* package */ AliasDescriptor(String contract) {
        this.contract = contract;
    }

    /**
     * Gets descriptor's contract.
     *
     * @return descriptor's contract.
     */
    public String getContract() {
        return contract;
    }

    /**
     * Gets descriptor's optional scope.
     *
     * @return descriptor's scope, if set explicitly.
     */
    public Optional<String> getScope() {
        return scope;
    }

    /**
     * Sets the descriptor's scope.
     *
     * @param scope descriptor's scope.
     * @return current instance.
     */
    public AliasDescriptor in(String scope) {
        this.scope = Optional.of(scope);

        return this;
    }

    /**
     * Gets descriptor's optional rank.
     *
     * @return descriptor's rank, if set explicitly.
     */
    public OptionalInt getRank() {
        return rank;
    }

    /**
     * Sets the descriptor's rank.
     *
     * @param rank descriptor's rank.
     * @return current instance.
     */
    public AliasDescriptor ranked(int rank) {
        this.rank = OptionalInt.of(rank);

        return this;
    }

    /**
     * Gets descriptor's qualifiers.
     *
     * @return descriptor's qualifiers
     */
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    /**
     * Adds a new descriptor's qualifier.
     *
     * @param annotation descriptor's qualifier.
     * @return current instance.
     */
    public AliasDescriptor qualifiedBy(Annotation annotation) {
        if (annotation != null) {
            qualifiers.add(annotation);
        }

        return this;
    }
}
