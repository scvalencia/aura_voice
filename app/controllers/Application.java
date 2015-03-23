package controllers;

import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.libs.Json;
import play.mvc.*;

import sound.SoundCloudAPI;
import views.html.*;

import java.util.Date;

@CorsComposition.Cors
public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result token(String path) {
        return ok("");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result uploadFile(Long id) {
        ObjectNode result = Json.newObject();
        JsonNode j = Controller.request().body().asJson();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        if(uploadFilePart != null) {
            SoundCloudAPI s = new SoundCloudAPI();
            try {
                Long urlId = s.upload(id.toString() + new Date().toString(), uploadFilePart.getFile());
                result.put("url", urlId);
            } catch(Exception e) {
                System.out.println(e.fillInStackTrace());
            }

        }
        return ok(Json.toJson(result));
    }

}
