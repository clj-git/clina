package clina;

import org.clina.core.MockCore;
import org.clina.core.models.CommitInfo;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zjh on 15-5-25.
 */


public class MockCoreTests {
    @Test
    public void initBareRepo() {
        System.out.println(MockCore.initBareRepo("darfoo", "cleantha" + System.currentTimeMillis()));
    }

    @Test
    public void viewRepoWithCommits() {
        List<CommitInfo> commits = MockCore.viewRepoWithCommits("root", "hehehe", "jihui_dev", 1, "hehe/");
        for (CommitInfo info : commits) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("YEAR " + simpleDateFormat.format(info.commitTime).toUpperCase());
        }
    }

    @Test
    public void viewProjectWithDefaultBranch() {
        MockCore.fileList(MockCore.getRepositoryDir("root", "hehehe"), "jihui_dev", ".");
    }

    @Test
    public void viewProjectWithSpecificBranch() {
        MockCore.fileList(MockCore.getRepositoryDir("root", "hehehe"), "jihui_dev", "hehe");
        MockCore.fileList(MockCore.getRepositoryDir("root", "hehehe"), "jihui_dev", "hehe/clea");
    }

    @Test
    public void getBranchesForSpecificCommit() {
        MockCore.getBranchesOfCommit(MockCore.getRepositoryDir("root", "hehehe"), "17fd66c156f508ab2cb8440af566029c8ba5cced");
    }

    @Test
    public void getTagsForSpecificCommit() {
        MockCore.getTagsOfCommit(MockCore.getRepositoryDir("root", "hehehe"), "cec4fa14603ec22861f55ba1982706ded094ebb4");
        MockCore.getTagsOfCommit(MockCore.getRepositoryDir("root", "hehehe"), "17fd66c156f508ab2cb8440af566029c8ba5cced");
    }

    @Test
    public void viewSpecificCommitInfo() {
        String[] commithashs = {
                "fa79137faba484d28078c0e7bbe79e46745774e4",
                "a9d0d12eccb12d550a3b74e585f820bae64d4e1c",
                "c7d99c246042ea7bd48ec15d759e0f9f47b0101b",
                "3fff559b37cb5a7fc9922209f0f90cb2b4702a0b",
                "c84b0e7c404e2ed791b5fa1f527a1c4886f62672",
                "17fd66c156f508ab2cb8440af566029c8ba5cced",
        };
        for (String id : commithashs) {
            MockCore.viewSpecificCommitDiffs(id, "root", "hehehe");
        }
    }

    @Test
    public void getParentPath() {
        for (String path : "hehe".split("/")) {
            System.out.println(path);
        }
    }

    @Test
    public void getRepoBasePath() {
        System.out.println(System.getProperty("user.home"));
        System.out.println(".".equals("."));
    }

    @Test
    public void getYearMonthDay() {
        Date date = new Date(System.currentTimeMillis()); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        System.out.println(String.format("%d-%d-%d", year, month, day));
    }

    @Test
    public void getBasePathFromEnv() {
        //System.out.println(System.getenv());
        Map<String, String> envmap = System.getenv();
        for (String key : System.getenv().keySet()) {
            System.out.println("key -> " + key);
            System.out.println("value -> " + envmap.get(key));
        }
    }
}
