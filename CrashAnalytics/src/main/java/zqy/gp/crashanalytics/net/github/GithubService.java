package zqy.gp.crashanalytics.net.github;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface GithubService {

    /**
     * <a herf="https://docs.github.com/en/rest/guides/basics-of-authentication#accepting-user-authorization">Github API Doc</a>
     */
    @POST("repos/{owner}/{repo}/issues")
    Call<ResponseBody> createIssue(@Path("owner") String owner, @Path("repo") String repo, @Body Issue issue);
}
