package net.aprendizajengrande.recommender;

import java.io.File;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExtractorMain {

    public static void main(String[] args) throws Exception {
        String gitRepoPath = args[0];
        String outputDBPath = args[1];

        System.out.println("Git dir: " + gitRepoPath);

        // Check that the first parameter points to a valid git repository
        // FileRepositoryBuilder builder = new FileRepositoryBuilder();
        // Repository repository = builder
        // .setGitDir(new File(gitRepoPath + "/.git")).readEnvironment()
        // .findGitDir().build();
        // Git git = new Git(repository);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "di/root-context.xml");

        GitLogExtractor extractor = context.getBean("gitLogExtractor",
                GitLogExtractor.class);
        extractor.extractLogInformation(new File(gitRepoPath), outputDBPath);
    }

}
