package de.sn.quarkus.businessfunctions;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.sn.quarkus.businessfunctions.model.Item;
import de.sn.quarkus.businessfunctions.model.Project;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class BFTest {
	
	private static long identifier;
	
	@Test
	@Transactional
	@Order(1)
	public void testProjectList() {
		//Check preloaded data
		List<Project> listProjects = Project.listAll();
		assertEquals("Test Projekt",listProjects.get(0).name);	
		
		//List test data hierarchy
		/*
		for (int i = 0; i < listProjects.size(); i++) {
			Project project = listProjects.get(i);
			System.out.println("Project: id= " + project.id +  " name= " + project.name);
			for (int j = 0; j < project.Items.size(); j++) {
				Item item = project.Items.get(j);
				Long parentItemId = -1L;
				if (item.item != null) {
					parentItemId = item.item.id; 
				}
				System.out.println("Item: id=" + item.id + " parent item id = " + parentItemId + " name=" + item.name + " level=" + item.level + " imageURL=" + item.imageURL);
			}
		}*/
	}
	@Test
	@Transactional
	@Order(2)
	public void testProjectAddNewRecordAndFindByNameLike() {
		//Add new record
		Project newProject = new Project();
		newProject.id= null;
		newProject.name = "MyTestProject";
		newProject.persist();	
		
		//Find first locomotive with certain address
		List<Project> myProject = Project.findAllByNameLike("My");
		assertEquals(1, myProject.size());
		BFTest.identifier = myProject.get(0).id;
	}
	
	@Test
	@Transactional
	@Order(3)
	public void testProjectFindAllRecordsWithPaging() {
		//Find all records with paging
		PanacheQuery<Project> pagedProjectList = Project.findAll();
		// make it use pages of 25 entries at a time
		pagedProjectList.page(Page.ofSize(25));
		assertEquals(1, pagedProjectList.pageCount());
		// get the first page
		List<Project> firstPage = pagedProjectList.list();
		assertThat("listsize", firstPage.size() > 0);		
		// get the xxx page
		List<Project> page2 = pagedProjectList.page(Page.of(2, 25)).list();
		assertEquals(0, page2.size());
	}
	
	@Test
	@Transactional
	@Order(4)
	public void testProjectUpdateRecord() {
		//Find first locomotive with certain address
		Project myProject =  Project.findById(BFTest.identifier);
	    myProject.name="UpdatedName";
		myProject.persist();
	    Project updatedProject = Project.findById(BFTest.identifier);
		assertEquals("UpdatedName", updatedProject.name);
	}
	
	@Test
	@Transactional
	@Order(5)
	public void testProjectDeleteRecord() {
		Project.deleteById(BFTest.identifier);
		Project deletedProject = Project.findById(BFTest.identifier);
		assertEquals(null, deletedProject);
	}
	
	@Test
	@Transactional
	@Order(10)
	public void testItemList() {
		//Check preloaded data
		List<Item> listItems = Item.listAll();
		Item.listAll(Sort.by("level"));
		assertEquals("main",listItems.get(0).name);	
	}
	
	@Test
	@Transactional
	@Order(11)
	public void testCreateAndFindItemByLevelAndProjectId() {
		Item newItem = new Item();
		newItem.project = new Project();
		newItem.project.id = 2L;
		newItem.name = "main";
		newItem.level = 0;
		newItem.imageURL = "main.jpg";
		newItem.persist();
				
		List<Item> itemList = Item.findByLevelAndProjectId(0, 2L);
		assertEquals("main", itemList.get(0).name);
		BFTest.identifier = itemList.get(0).id;
	}
	
	@Test
	@Transactional
	@Order(12)
	public void testItemUpdateRecord() {
		//Find first locomotive with certain address
		Item item =  Item.findById(BFTest.identifier);
	    item.name="UpdatedName";
		item.persist();
	    Item updatedItem = Item.findById(BFTest.identifier);
		assertEquals("UpdatedName", updatedItem.name);
	}
	
	@Test
	@Transactional
	@Order(13)
	public void testItemDeleteRecord() {
		Item.deleteById(BFTest.identifier);
		Item deletedItem = Item.findById(BFTest.identifier);
		assertEquals(null, deletedItem);
	}
	
	@Test
	@Order(20)
    public void testRESTProjectGetAll() {
        given()
          .when().get("/projects")
          .then()
             .statusCode(OK.getStatusCode())
             //.log().body()
             .body("id", notNullValue())
         	 .body("name", notNullValue());
    }
	@Test
	@Order(21)
    public void testRESTProjectGetById() {
     	given()
          .when().get("/projects/1")
          .then()
          	 //.log().body()
             .statusCode(OK.getStatusCode())
             .body("id", equalTo(1))
             .body("name", equalTo("Test Projekt"));
    }
	
	@Test
	@Order(22)
    public void testRESTProjectAddNewProject() {
        Project project = new Project();
        project.id = null;
        project.name = "Test Projekt 3";
        //Items will not be added here
        
		ValidatableResponse response = given().contentType("application/json")
                .body(project)
        		.when().post("/projects")
                .then()
	                //.log().body()
	                .statusCode(CREATED.getStatusCode())
                	.body("id", notNullValue())
                	.body("name", equalTo("Test Projekt 3"));

        BFTest.identifier = Long.parseLong(response.extract().body().
        		jsonPath().get("id").toString());
        assertEquals(true,true);
    }
	
	  @Test
	   @Order(23)
	    public void testRestPut() {
	      Project project = new Project();
	      project.id = BFTest.identifier;
	      project.name= "TestTest";
		  ValidatableResponse response = given().contentType("application/json")
	        		.body(project)
	                .when().put("/projects")
	                .then()
	                	//.log().body()
	                	.statusCode(OK.getStatusCode())
	                	//.body("id", is(book.id)) 
	                	//-> this doesn't work for long or double values. Need to use JSON Path after
	                	.body("name", equalTo("TestTest"));
	        
	        Long id = Long.parseLong(response.extract().body().
	        		jsonPath().get("id").toString());
	        assertEquals(id, (Long) BFTest.identifier);
	    }
	 
	@Test
    @Order(29)
    public void testRESTProjectDelete() {
	     given()
              .when().delete("/projects/"+ BFTest.identifier)
              .then().statusCode(NO_CONTENT.getStatusCode());
    }
}