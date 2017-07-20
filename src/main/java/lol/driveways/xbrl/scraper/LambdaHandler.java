package lol.driveways.xbrl.scraper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<Integer, String> {
    @Override
    public String handleRequest(Integer input, Context context) {
        Edgar edgar = new Edgar();
        edgar.scrape(context);
        return "Done.";
    }
}
