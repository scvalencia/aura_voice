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
    
    public static Result uploadFile(Long id) {
        ObjectNode result = Json.newObject();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        System.out.println("BODY");
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        System.out.println("FILE");
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
