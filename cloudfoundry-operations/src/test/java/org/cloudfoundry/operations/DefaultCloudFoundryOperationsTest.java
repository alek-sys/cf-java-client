/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultCloudFoundryOperationsTest extends AbstractOperationsTest {

    private final DefaultCloudFoundryOperations operations = DefaultCloudFoundryOperations.builder()
        .cloudFoundryClient(this.cloudFoundryClient)
        .dopplerClient(this.dopplerClient)
        .organization(TEST_ORGANIZATION_NAME)
        .space(TEST_SPACE_NAME)
        .uaaClient(this.uaaClient)
        .build();

    @Test
    public void advanced() {
        assertThat(this.operations.advanced()).isNotNull();
    }

    @Test
    public void applications() {
        assertThat(this.operations.applications()).isNotNull();
    }

    @Test
    public void buildpacks() {
        assertThat(this.operations.buildpacks()).isNotNull();
    }

    @Test
    public void domains() {
        assertThat(this.operations.domains()).isNotNull();
    }

    @Test
    public void networkPolicies() {
        assertThat(this.operations.networkPolicies()).isNotNull();
    }

    @Test
    public void organizationAdmin() {
        assertThat(this.operations.organizationAdmin()).isNotNull();
    }

    @Test
    public void organizations() {
        assertThat(this.operations.organizations()).isNotNull();
    }

    @Test
    public void routes() {
        assertThat(this.operations.routes()).isNotNull();
    }

    @Test
    public void serviceAdmin() {
        assertThat(this.operations.serviceAdmin()).isNotNull();
    }

    @Test
    public void services() {
        assertThat(this.operations.services()).isNotNull();
    }

    @Test
    public void spaceAdmin() {
        assertThat(this.operations.spaceAdmin()).isNotNull();
    }

    @Test
    public void spaces() {
        assertThat(this.operations.spaces()).isNotNull();
    }

    @Test
    public void stacks() {
        assertThat(this.operations.stacks()).isNotNull();
    }

    @Test
    public void userAdmin() {
        assertThat(this.operations.userAdmin()).isNotNull();
    }

    @Test
    public void shouldNotCacheOrgIdWhenCacheDurationIsNotSet() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.operations.getOrganizationId()
            .as(StepVerifier::create)
            .expectNext(TEST_ORGANIZATION_ID)
            .verifyComplete();

        requestOrganizations(this.cloudFoundryClient, "updated-organization-id");

        this.operations.getOrganizationId()
            .as(StepVerifier::create)
            .expectNext("updated-organization-id")
            .verifyComplete();
    }

    @Test
    public void shouldNotCacheSpaceIdWhenCacheDurationIsNotSet() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSpaces(this.cloudFoundryClient, TEST_SPACE_ID);

        this.operations.getSpaceId()
            .as(StepVerifier::create)
            .expectNext(TEST_SPACE_ID)
            .verifyComplete();

        requestSpaces(this.cloudFoundryClient, "updated-space-id");

        this.operations.getSpaceId()
            .as(StepVerifier::create)
            .expectNext("updated-space-id")
            .verifyComplete();
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(TEST_ORGANIZATION_NAME)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .metadata(fill(Metadata.builder()).id(organizationId).build())
                        .entity(fill(OrganizationEntity.builder(), "organization-entity-")
                            .build())
                        .build())
                    .build()));

    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .list(ListSpacesRequest.builder()
                .name(TEST_SPACE_NAME)
                .organizationId(TEST_ORGANIZATION_ID)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder()).metadata(fill(Metadata.builder()).id(spaceId).build())
                        .build())
                    .build()));
    }
}
