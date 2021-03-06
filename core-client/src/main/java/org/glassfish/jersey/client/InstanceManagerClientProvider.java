/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.jersey.client;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;

import org.glassfish.jersey.InstanceManagerProvider;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InstanceManagerSupplier;
import org.glassfish.jersey.spi.inject.InstanceManager;

/**
 * Extension of {@link InstanceManagerProvider} which contains helper static methods
 * that extract {@link InstanceManager} from client specific JAX-RS components.
 * <p>
 * See javadoc of {@link InstanceManagerProvider} for more details.
 * </p>
 *
 * @see InstanceManagerProvider
 * @author Miroslav Fuksa
 * @since 2.6
 */
public class InstanceManagerClientProvider extends InstanceManagerProvider {

    /**
     * Extract and return instance manager from {@link javax.ws.rs.client.ClientRequestContext clientRequestContext}.
     * The method can be used to inject custom types into a {@link javax.ws.rs.client.ClientRequestFilter}.
     *
     * @param clientRequestContext Client request context.
     *
     * @return instance manager.
     *
     * @throws java.lang.IllegalArgumentException when {@code clientRequestContext} is not a default
     * Jersey implementation provided by Jersey as argument in the
     * {@link javax.ws.rs.client.ClientRequestFilter#filter(javax.ws.rs.client.ClientRequestContext)} method.
     */
    public static InstanceManager getInstanceManager(ClientRequestContext clientRequestContext) {
        if (!(clientRequestContext instanceof InstanceManagerSupplier)) {
            throw new IllegalArgumentException(
                    LocalizationMessages
                            .ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(clientRequestContext.getClass().getName()));
        }
        return ((InstanceManagerSupplier) clientRequestContext).getInstanceManager();
    }

    /**
     * Extract and return instance manager from {@link javax.ws.rs.client.ClientResponseContext clientResponseContext}.
     * The method can be used to inject custom types into a {@link javax.ws.rs.client.ClientResponseFilter}.
     *
     * @param clientResponseContext Client response context.
     *
     * @return instance manager.
     *
     * @throws java.lang.IllegalArgumentException when {@code clientResponseContext} is not a default
     * Jersey implementation provided by Jersey as argument in the
     * {@link javax.ws.rs.client.ClientResponseFilter#filter(javax.ws.rs.client.ClientRequestContext, javax.ws.rs.client.ClientResponseContext)}
     * method.
     */
    public static InstanceManager getInstanceManager(ClientResponseContext clientResponseContext) {
        if (!(clientResponseContext instanceof InstanceManagerSupplier)) {
            throw new IllegalArgumentException(
                    LocalizationMessages
                            .ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(clientResponseContext.getClass().getName()));
        }
        return ((InstanceManagerSupplier) clientResponseContext).getInstanceManager();
    }

}

