/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.vick.observability.api;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionList;
import io.fabric8.kubernetes.api.model.apiextensions.DoneableCustomResourceDefinition;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.wso2.vick.observability.api.crd.CellCRD;
import org.wso2.vick.observability.api.crd.CellCRDDoneable;
import org.wso2.vick.observability.api.crd.CellCRDList;
import org.wso2.vick.observability.api.model.CellInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API to expose kubernetes level details.
 */
@Path("/kubernetes-details")
public class KubernetesAPI {
    private Set<CellInfo> cellInfoMap = new HashSet<>();
    private static final String CELL_CRD_NAME = "cells.vick.wso2.com";

    @GET
    @Path("/cells/namespaces")
    @Produces("application/json")
    public Response getCellNamespace() {
        KubernetesClient client = new DefaultKubernetesClient();
        CustomResourceDefinitionList crds = client.customResourceDefinitions().list();
        List<CustomResourceDefinition> crdsItems = crds.getItems();
        CustomResourceDefinition cellCRD = null;
        for (CustomResourceDefinition crd : crdsItems) {
            ObjectMeta metadata = crd.getMetadata();
            if (metadata != null) {
                String name = metadata.getName();
                if (CELL_CRD_NAME.equals(name)) {
                    cellCRD = crd;
                    break;
                }
            }
        }
        if (cellCRD != null) {
            NonNamespaceOperation<CellCRD, CellCRDList, CellCRDDoneable, Resource<CellCRD, CellCRDDoneable>> cellClient =
                    client.customResources(cellCRD, CellCRD.class, CellCRDList.class, CellCRDDoneable.class);
            CellCRDList cells = cellClient.list();
        }
        return Response.ok().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .entity(cellInfoMap)
                .build();
    }

    @POST
    @Path("/cells/namespaces")
    @Produces("application/json")
    public Response addCellNamespaceMapping(CellInfo cellInfo) {
        this.cellInfoMap.add(cellInfo);
        return Response.ok().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .build();
    }
}
