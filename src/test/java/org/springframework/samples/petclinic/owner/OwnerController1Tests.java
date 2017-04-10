package org.springframework.samples.petclinic.owner;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerController;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
// @WebAppConfiguration
// @WebMvcTest(OwnerController.class)

// @WebIntegrationTest({"server.port=8080", "management.port=0"})
// @SpringApplicationConfiguration(classes = PetClinicApplication.class)

public class OwnerController1Tests {

	private static final int TEST_OWNER_ID = 1;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void init() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Autowired
	private OwnerRepository ownerService;

	@Test
	public void testProcessCreationFormHasErrors() throws Exception {

		mockMvc.perform(post("/owners/new").param("firstName", "sambit")
				.param("lastName", "Bloggs").param("city", "London"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(
						model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@Transactional
	public void testProcessPetCreationSuccess() throws Exception {

		ResultActions rAction = mockMvc.perform(post("/owners/new")
				.param("firstName", "sambit1").param("lastName", "Bloggs")
				.param("city", "London").param("address", "cs pur")
				.param("telephone", "0123456789"));

		Owner owner = ownerService.findByLastName("Bloggs").iterator().next();
		assertEquals("sambit1", owner.getFirstName());

		rAction.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/owners/" + owner.getId()));

	}

	@Test
	@Transactional
	public void testProcessUpdateOwnerFormSuccess() throws Exception {

		String lastNameEdit = "Mohanty";

		Owner owner = new Owner();
		owner.setFirstName("pankaj");
		owner.setLastName("Moha");
		owner.setAddress("bhubanwswar");
		owner.setCity("bhubaneswar");
		owner.setTelephone("1234567890");

		ownerService.save(owner);

		mockMvc.perform(post("/owners/{ownerId}/edit", owner.getId())
				.param("firstName", owner.getFirstName())
				.param("lastName", lastNameEdit)
				.param("address", owner.getAddress())
				.param("city", owner.getCity())
				.param("telephone", owner.getTelephone()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));

		Owner editOwner = ownerService.findById(owner.getId());

		assertEquals(lastNameEdit, editOwner.getLastName());
	}

	@Test
	@Transactional
	public void testShowOwner() throws Exception {

		Owner owner = new Owner();
		owner.setFirstName("jeetendra");
		owner.setLastName("samal");
		owner.setAddress("bhubanwswar");
		owner.setCity("bhubaneswar");
		owner.setTelephone("1234567890");

		ownerService.save(owner);

		mockMvc.perform(get("/owners/{ownerId}", owner.getId()))
				.andExpect(model().attribute("owner", hasProperty("firstName", is("jeetendra"))))
				.andExpect(model().attribute("owner", hasProperty("lastName", is("samal"))))
				.andExpect(view().name("owners/ownerDetails"))
				
				;

	}

}
