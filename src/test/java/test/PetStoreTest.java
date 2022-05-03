package test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import petStore.AddPet;
import petStore.Category;
import petStore.PetStatus;
import petStore.Tag;

import java.util.ArrayList;
import java.util.List;

public class PetStoreTest {

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI ="https://petstore.swagger.io/v2";
    }
    @Test
    public void petFindByStatusTest(){
        Response response =
                RestAssured
                        .given()
                        .when()
                        .get("/pet/findByStatus?status=available")
                        .then()
                        .statusCode(200)
                        .extract().response();

        ObjectMapper mapper = new ObjectMapper();
        List<PetStatus> allPetStatus = response.as(List.class, ObjectMapperType.JACKSON_2);
        //ArrayList<PetStatus> allPetStatus = response.as(ArrayList.class,ObjectMapperType.JACKSON_2);
        List<PetStatus> pet = mapper.convertValue(allPetStatus, new TypeReference<List<PetStatus>>() {});

        for(int i=0; i<pet.size(); i++){

            String actualStatus = pet.get(i).getStatus();
            String actualName = pet.get(i).getName();
            Long actualCategoryId = pet.get(i).getCategory().getId();
            Long expectedCategoryId = 12L;
            String expectedName= "doggie";

            //Verify if the Status is available
            Assertions.assertEquals("available", actualStatus, "The status is not set to available");

            //Verify the name "doggie" with category id 12 on the List
            boolean actual = actualCategoryId.equals(expectedCategoryId) && actualName.equals(expectedName);
            Assertions.assertTrue(actual,"The name doggie with category id 12 does not exist");

        }
    }
    //Random Name generator method
    public String randomName(){
        int length = 5;
        boolean useLetter = true;
        boolean useNumbers = false;

        return RandomStringUtils.random(length,useLetter, useNumbers);
    }
    @Test
     public void addPetTest(){

      //  RequestSpecification request = RestAssured.given();

        AddPet addPets = new AddPet();

        addPets.setName(randomName());
        addPets.setId(20L);
        addPets.setStatus("available");

        Category addCategory = new Category();
        addCategory.setId(123L);
        addCategory.setName("addMyPet");

        addPets.setCategory(addCategory);

        Tag addTags = new Tag();
        addTags.setName("tagName");
        addTags.setId(789L);

        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(addTags);
        addPets.setTags(tags);

        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("");

        addPets.setPhotoUrls(photoUrls);

        //passing the Object
        Response response = RestAssured
                .given()
                .when()
                .header("Content-Type", "Application/json")
                .and()
                .body(addPets)
                .post("/pet")
                .then()
                .statusCode(200)
                .extract().response();

        AddPet addPet = response.as(AddPet.class, ObjectMapperType.JACKSON_2);

        boolean actual = addPet.getCategory().getId()==123 && addPet.getCategory().getName().equals("addMyPet")
                && addPet.getStatus().equals("available");

        Assertions.assertTrue(actual, "The pet is not added");
        Assertions.assertEquals(20,addPet.getId());
        //return addPet.getId();
    }

    @Test
    public void retrieveAddPetTest(){
        Response response =
                RestAssured
                        .given()
                        .when()
                       // .get("/pet/+ addPetTest)
                        .get("/pet/20")
                        .then()
                        .statusCode(200)
                        .statusLine("HTTP/1.1 200 OK")
                        .extract().response();

        System.out.println(response.body().asString());
    }
}
