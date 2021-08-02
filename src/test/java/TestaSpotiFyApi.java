import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;


public class TestaSpotiFyApi {

    public static String accessToken = "";
    public static String payload = "{\"name\":\"New Playlist69\",\"description\":\"New playlist description\",\"public\":false}";

    @BeforeAll
    public static void authenticationSpotify(){
        String authToken = EncodeToken.getAuthToken("1bc8a54f257f444085203f94964879cc", "a6b0f17941da4a73af0156f05c3a8aff");
        generateAccessToken(authToken);
    }

    @BeforeEach
    public void antesTestes(){
        RestAssured.baseURI = "https://api.spotify.com";
        RestAssured.basePath = "/v1/";
    }

    private static void generateAccessToken(String authToken) {
        RestAssured.baseURI = "https://accounts.spotify.com/api";

        Response response = given().
                header("Authorization","Basic "+authToken).
                contentType("application/x-www-form-urlencoded").
                formParam("grant_type","client_credentials").
                log().all().
                when().
                post("token");

        accessToken = response.jsonPath().get("access_token");
    }

    /**
     * Retorna Playlists de um Usuario do Spotify
     * URL API: https://api.spotify.com/v1/users/{user_id}/playlists
     */
    @Test
    @DisplayName("Retornar playlists do usuario e verifica se tem uma playlist nomeada my bjork faves")
    public void shouldReturnPlaylistsOfUser(){
                given().
                        auth().oauth2(this.accessToken).
                        accept(ContentType.JSON).
                        when().
                        get("users/{user_id}/playlists","jadersaldanha").
                        then().
                        log().
                        body().
                        statusCode(200).
                        body("items.name",hasItem("my bjork faves")).
                        extract().
                        response();
    }

    @Test
    @DisplayName("Verifica se contem a musica DAMN DANIEL (feat. Yung Baby Tate) na Playlist do usuario")
    public void shouldReturnMusicOnPlaylist(){
        given().
                auth().oauth2(this.accessToken).
                accept(ContentType.JSON).
                when().
                get("playlists/{playlist_id}/tracks","3dDLMcv86Z2jSyiz9zMAoc").
                then().
                log().
                body().
                statusCode(200).
                body("items.track.name",hasItem("DAMN DANIEL (feat. Yung Baby Tate)")).
                extract().
                response();
    }

    @Test
    @DisplayName("Verifica se contem a musica Arena - Remixed by The Japanese Popstars no TRON: Legacy Reconfigured no album Tron do Daft Punk")
    public void shouldReturnMusicOnPlaylist2(){
        given().
                auth().oauth2(this.accessToken).
                accept(ContentType.JSON).
                when().
                get("albums/{id}/tracks","382ObEPsp2rxGrnsizN5TX").
                then().
                log().
                body().
                statusCode(200).
                body("items.name",hasItem("Arena - Remixed by The Japanese Popstars")).
                extract().
                response();
    }

    @Test
    @DisplayName("Cria uma playlist na conta do usuário")
    public void shouldReturnMusicOnPlaylist3(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer BQCi8vQ-VNhwbZl5yXhVlvx7lSHBKL9mNWtQcw1aO8nS_BXAzLWBf-gHYQAdqlwbp2z0TiAessvcbxpbQMALTUuK4Lg4mQRaTKTJN9nqaKnfcylCnsZxyZ1pc6J5ntegeDd1j4tZhmAu9iSQdTuf3v4yHZ323Rn2aSsEKAqWQAsf3iA0o_4ZdFU-anHlZlPhjZuQ5Eratb-7gTb8zt8jNFLI7Ntj_KWp9_--fH87I0yptLMXK1_bvA5YnkHUEg8iOkenxSKVqMU_zF9zvibPdFeJZshB")
                .body(payload)
                .when()
                .post("users/{user_id}/playlists","jadersaldanha");
    }
}
