package net.aprendizajengrande.recommender;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;

import net.aprendizajengrande.recommender.db.LogDB;
import net.aprendizajengrande.recommender.model.Commit;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link GitLogExtractor}
 * 
 * @author Germán E. Trouillet
 */
public class GitLogExtractorTest {

    private GitLogExtractor gitLogExtractor;
    private ProcessFactory processFactory;
    private LogDB logDB;

    @Before
    public void setUp() {
        processFactory = createMock(ProcessFactory.class);
        logDB = createMock(LogDB.class);
        gitLogExtractor = new GitLogExtractor(processFactory, logDB);
    }

    @Test
    public void souldParseGitLogInfo() throws Exception {
        File gitRepoPath = createMock(File.class);
        Process process = createMock(Process.class);
        expect(
                processFactory.createProccess(gitRepoPath, "git", "log",
                        "--format=format:commit: \"%H\", author: \"%an\"",
                        "--name-status", "--no-merges")).andReturn(process);

        expect(process.getInputStream()).andReturn(
                getClass().getResourceAsStream("/gitLog/GitLogBrief.log"));
        expect(process.waitFor()).andReturn(0);
        logDB.addCommit(anyObject(Commit.class));
        expectLastCall().times(4);

        replay(processFactory, logDB, gitRepoPath, process);

        gitLogExtractor.extractLogInformation(gitRepoPath, "/out/");

        verify(processFactory, logDB, gitRepoPath, process);
    }

}
