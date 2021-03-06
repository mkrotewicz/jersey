/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.jersey.server.internal.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Parameter.Source;
import org.glassfish.jersey.server.spi.internal.ValueSupplierProvider;
import org.glassfish.jersey.spi.inject.InstanceManager;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.utilities.cache.Cache;
import org.glassfish.hk2.utilities.cache.Computable;

/**
 * Value factory provider that delegates the injection target lookup to the underlying injection provider.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
@Singleton
class DelegatedInjectionValueSupplierProvider implements ValueSupplierProvider {

    private final ContextInjectionResolver resolver;

    /**
     * Injection constructor.
     *
     * @param instanceManager instance manager.
     */
    @Inject
    public DelegatedInjectionValueSupplierProvider(InstanceManager instanceManager) {
        ContextInjectionResolver result = null;
        for (InjectionResolver r : Providers.getProviders(instanceManager, InjectionResolver.class)) {
            if (ContextInjectionResolver.class.isInstance(r)) {
                result = ContextInjectionResolver.class.cast(r);
                break;
            }
        }
        resolver = result;
    }

    @Override
    public Supplier<?> getValueSupplier(final Parameter parameter) {
        final Source paramSource = parameter.getSource();
        if (paramSource == Parameter.Source.CONTEXT) {

            final Injectee effectiveInjectee = getInjectee(parameter);

            return (Supplier<Object>) () -> resolver.resolve(effectiveInjectee, null);
        }
        return null;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.LOW;
    }

    /**
     * We do not want to create a new descriptor instance for every and each method invocation.
     */
    static final Cache<Parameter, ActiveDescriptor> descriptorCache =
            new Cache<>(new Computable<Parameter, ActiveDescriptor>() {

                @Override
                public ActiveDescriptor compute(Parameter parameter) {
                    Class<?> rawType = parameter.getRawType();
                    if (rawType.isInterface() && !(parameter.getType() instanceof ParameterizedType)) {
                        return createDescriptor(rawType);
                    }
                    return null;
                }
            });

    private static Injectee getInjectee(final Parameter parameter) {
        return new InjecteeImpl() {

            private final Class<?> rawType = parameter.getRawType();

            {
                setRequiredType(parameter.getType());
                setRequiredQualifiers(Collections.<Annotation>emptySet());
                final ActiveDescriptor proxyDescriptor = descriptorCache.compute(parameter);
                if (proxyDescriptor != null) {
                    setInjecteeDescriptor(proxyDescriptor);
                }
            }

            @Override
            public Class<?> getInjecteeClass() {
                return rawType;
            }
        };
    }

    private static <T> AbstractActiveDescriptor<T> createDescriptor(final Class<T> clazz) {
        return new AbstractActiveDescriptor<T>() {
            @Override
            public Class<T> getImplementationClass() {
                return clazz;
            }

            @Override
            public Type getImplementationType() {
                return getImplementationClass();
            }

            @Override
            public T create(ServiceHandle sh) {
                return null;
            }

            @Override
            public Boolean isProxyForSameScope() {
                return false;
            }

            @Override
            public synchronized String getScope() {
                return RequestScoped.class.getName();
            }
        };
    }
}
