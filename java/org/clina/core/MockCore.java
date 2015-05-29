package org.clina.core;

import org.clina.core.models.CommitInfo;
import org.clina.core.models.DiffInfo;
import org.clina.core.models.FileInfo;
import org.clina.core.utils.FileUtil;
import org.clina.core.utils.StringUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zjh on 15-5-17.
 */

public class MockCore {
    public static String basepath = String.format("%s/.gitbucket", System.getProperty("user.home"));

    public static File getRepositoryDir(String owner, String repository) {
        String RepositoryHome = String.format("%s/repositories", basepath);

        String repoDir = String.format("%s/%s/%s.git", RepositoryHome, owner, repository);
        return new File(repoDir);
    }

    public static boolean initRepository(File gitDir, boolean bare) {
        try {
            (new RepositoryBuilder().setGitDir(gitDir).build()).create(bare);
            //new RepositoryBuilder().setGitDir(gitDir).setBare().build().create();
            return true;
        } catch (Exception e) {
            //repository already exists
            return false;
        }
    }

    public static boolean deleteRepository(String owner, String repository) {
        try {
            FileUtil.delete(getRepositoryDir(owner, repository));
            File ownerdir = new File(String.format("%s/repositories/%s", basepath, owner));
            //如果某一个用户目录下已经没有repo了那么就把用户路径也删除掉
            if (ownerdir.list().length == 0) {
                FileUtil.delete(ownerdir);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns RevCommit from the commit or tag id.
     *
     * @param git      the Git object
     * @param objectId the ObjectId of the commit or tag
     * @return the RevCommit for the specified commit or tag
     */
    public static RevCommit getRevCommitFromId(Git git, ObjectId objectId) {
        RevWalk revWalk = new RevWalk(git.getRepository());
        RevCommit commit = null;
        try {
            RevObject object = revWalk.parseAny(objectId);
            if (object instanceof RevTag) {
                RevTag revTag = (RevTag) object;
                commit = revWalk.parseCommit(revTag.getObject());
            } else {
                commit = revWalk.parseCommit(objectId);
            }
            revWalk.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commit;
    }

    public static RevCommit getLastModifiedCommit(Git git, RevCommit startCommit, String path) {
        try {
            return git.log().add(startCommit).addPath(path).setMaxCount(1).call().iterator().next();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (MissingObjectException e) {
            e.printStackTrace();
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class TempFileInfo {
        ObjectId id;
        FileMode mode;
        String path;
        String name;
        String[] linkUrl;

        public TempFileInfo(ObjectId id, FileMode mode, String path, String name, String[] linkUrl) {
            this.id = id;
            this.mode = mode;
            this.path = path;
            this.name = name;
            this.linkUrl = linkUrl;
        }
    }

    /**
     * Returns the list of latest RevCommit of the specified paths.
     *
     * @param git      the Git object
     * @param paths    the list of paths
     * @param revision the branch name or commit id
     * @return the list of latest commit
     */
    public static HashMap<String, RevCommit> getLatestCommitFromPaths(Git git, List<String> paths, String revision) {
        HashMap<String, RevCommit> result = new HashMap<String, RevCommit>();
        try {
            RevCommit start = getRevCommitFromId(git, git.getRepository().resolve(revision));

            for (String path : paths) {
                RevCommit commit = git.log().add(start).addPath(path).setMaxCount(1).call().iterator().next();
                result.put(path, commit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Returns the first line of the commit message.
     */
    public static String getSummaryMessage(String fullMessage, String shortMessage) {
        int i = fullMessage.trim().indexOf("\n");
        String result;
        if (i >= 0) {
            result = fullMessage.trim().substring(0, i).trim();
        } else {
            result = fullMessage;
        }

        if (result.length() > shortMessage.length()) {
            result = shortMessage;
        }

        return result;
    }

    /**
     * @param git
     * @param revision the branch name or commitid
     * @param path
     * @return
     */
    public static List<FileInfo> getFileList(Git git, String revision, final String path) {
        List<TempFileInfo> list = new ArrayList<TempFileInfo>();

        RevWalk revWalk = new RevWalk(git.getRepository());
        try {
            ObjectId objectId = git.getRepository().resolve(revision);
            RevCommit revCommit = revWalk.parseCommit(objectId);

            TreeWalk treeWalk = new TreeWalk(git.getRepository());
            treeWalk.addTree(revCommit.getTree());
            if (!path.equals(".")) {
                treeWalk.setRecursive(true);
                treeWalk.setFilter(new TreeFilter() {
                    boolean stopRecursive = false;

                    @Override
                    public boolean include(TreeWalk treeWalk) throws MissingObjectException, IncorrectObjectTypeException, IOException {
                        String targetPath = treeWalk.getPathString();
                        if ((path + "/").startsWith(targetPath)) {
                            return true;
                        } else if (targetPath.startsWith(path + "/") && targetPath.substring(path.length() + 1).indexOf("/") < 0) {
                            stopRecursive = true;
                            treeWalk.setRecursive(false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public boolean shouldBeRecursive() {
                        return !stopRecursive;
                    }

                    @Override
                    public TreeFilter clone() {
                        return this;
                    }
                });
            }

            while (treeWalk.next()) {
                // submodule
                String[] linkUrl = null;

                list.add(new TempFileInfo(treeWalk.getObjectId(0), treeWalk.getFileMode(0), treeWalk.getPathString(), treeWalk.getNameString(), linkUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> pathList = new ArrayList<String>();

        for (TempFileInfo fileInfo : list) {
            pathList.add(fileInfo.path);
        }

        HashMap<String, RevCommit> commits = getLatestCommitFromPaths(git, pathList, revision);

        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        for (TempFileInfo fileInfo : list) {
            RevCommit commit = commits.get(fileInfo.path);

            fileInfoList.add(new FileInfo(
                    fileInfo.id,
                    (fileInfo.mode == FileMode.TREE || fileInfo.mode == FileMode.GITLINK),
                    fileInfo.name,
                    getSummaryMessage(commit.getFullMessage(), commit.getShortMessage()),
                    commit.getName(),
                    commit.getAuthorIdent().getWhen(),
                    commit.getAuthorIdent().getName(),
                    commit.getAuthorIdent().getEmailAddress(),
                    fileInfo.linkUrl));
        }

        Collections.sort(fileInfoList, new Comparator<FileInfo>() {
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.isDirectory && !o2.isDirectory) {
                    return 1;
                } else if (!o1.isDirectory && o2.isDirectory) {
                    return -1;
                } else {
                    return o1.name.compareTo(o2.name);
                }
            }
        });

        return fileInfoList;
    }

    public static List<String> getParentPaths(String path) {
        //to avoid null pointer exception
        List<String> parentPaths = new ArrayList<String>();
        if (!path.equals(".")) {
            parentPaths = Arrays.asList(path.split("/"));
        }
        return parentPaths;
    }

    public static RevCommit getLastModifiedCommit(String owner, String repository, String revstr, String path) {
        try {
            Git git = Git.open(getRepositoryDir(owner, repository));
            ObjectId objectId = git.getRepository().resolve(revstr);
            RevCommit lastModifiedCommit;
            RevCommit commit = getRevCommitFromId(git, objectId);
            if (path.equals(".")) {
                lastModifiedCommit = commit;
            } else {
                lastModifiedCommit = getLastModifiedCommit(git, commit, path);
            }
            return lastModifiedCommit;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FileInfo> getRepoFiles(String owner, String repository, String revstr, String path) {
        try {
            Git git = Git.open(getRepositoryDir(owner, repository));
            ObjectId objectId = git.getRepository().resolve(revstr);
            return getFileList(git, revstr, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param gitDir
     * @param revstr branch name or commitid
     * @param path
     */
    //clojure中调用java代码是无法使用String...这样的不定参数的
    public static void fileList(File gitDir, String revstr, String path) {
        try {
            Git git = Git.open(gitDir);

            ObjectId objectId = git.getRepository().resolve(revstr);

            RevCommit lastModifiedCommit;
            RevCommit commit = getRevCommitFromId(git, objectId);
            if (path.equals(".")) {
                lastModifiedCommit = commit;
            } else {
                lastModifiedCommit = getLastModifiedCommit(git, commit, path);
            }

            if (lastModifiedCommit != null) {
                //getName get the commit hash
                System.out.println(String.format("last commit info -> %s %s", lastModifiedCommit.getName(), lastModifiedCommit.getFullMessage()));
            }

            List<FileInfo> files = getFileList(git, revstr, path);

            List<String> parentPaths = null;
            if (!path.equals(".")) {
                parentPaths = Arrays.asList(path.split("/"));
            }

            if (parentPaths != null) {
                for (String ppath : parentPaths) {
                    System.out.println("parent path -> " + ppath);
                }
            }

            for (FileInfo fileInfo : files) {
                System.out.println("filename -> " + fileInfo.name);
                System.out.println("is directory -> " + fileInfo.isDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get object content of the given object id as byte array from the Git repository.
     *
     * @param git            the Git object
     * @param id             the object id
     * @param fetchLargeFile if false then returns None for the large file
     * @return the byte array of content or None if object does not exist
     */
    public static byte[] getContentFromId(Git git, ObjectId id, Boolean fetchLargeFile) {
        try {
            ObjectLoader loader = git.getRepository().getObjectDatabase().open(id);
            if (fetchLargeFile == false && FileUtil.isLarge(loader.getSize())) {
                return null;
            } else {
                ObjectDatabase db = git.getRepository().getObjectDatabase();
                return db.open(id).getBytes();
            }
        } catch (IOException e) {
            System.out.println("there is a -> AnyObjectId[0000000000000000000000000000000000000000]");
            return new byte[]{};
        }
    }

    public static String getDiffContent(byte[] contentFromId) {
        if (FileUtil.isText(contentFromId)) {
            return StringUtil.convertFromByteArray(contentFromId);
        } else {
            return "";
        }
    }

    public static List<DiffInfo> getDiffs(Git git, String from, String to, Boolean fetchContent) {
        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();

        try {
            oldTreeIter.reset(reader, git.getRepository().resolve(from + "^{tree}"));
            newTreeIter.reset(reader, git.getRepository().resolve(to + "^{tree}"));
            List<DiffEntry> diffEntries = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            List<DiffInfo> diffInfos = new ArrayList<DiffInfo>();
            for (DiffEntry diff : diffEntries) {
                if (!fetchContent || FileUtil.isImage(diff.getOldPath()) || FileUtil.isImage(diff.getNewPath())) {
                    diffInfos.add(new DiffInfo(diff.getChangeType(), diff.getOldPath(), diff.getNewPath(), "", ""));
                } else {
                    System.out.println("oldid -> " + diff.getOldId().toObjectId());
                    System.out.println("newid -> " + diff.getNewId().toObjectId());
                    diffInfos.add(new DiffInfo(diff.getChangeType(), diff.getOldPath(), diff.getNewPath(),
                            getDiffContent(getContentFromId(git, diff.getOldId().toObjectId(), false)),
                            getDiffContent(getContentFromId(git, diff.getNewId().toObjectId(), false))));
                }
            }
            return diffInfos;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return new ArrayList<DiffInfo>();
    }

    public static List<RevCommit> getCommitLogForDiffs(Iterator<RevCommit> i, List<RevCommit> logs) {
        if (i.hasNext() && logs.size() < 2) {
            logs.add(i.next());
            return getCommitLogForDiffs(i, logs);
        } else {
            return logs;
        }
    }

    public static Tuple<List<CommitInfo>, Boolean> getCommitLogForLog(Iterator<RevCommit> i, int count, List<CommitInfo> logs, int limit, int fixedPage) {
        if (i.hasNext() && (limit <= 0 || logs.size() < limit)) {
            RevCommit commit = i.next();
            if (limit <= 0 || (fixedPage - 1) * limit <= count) {
                logs.add(new CommitInfo(commit));
            }
            return getCommitLogForLog(i, count + 1, logs, limit, fixedPage);
        } else {
            return new Tuple<List<CommitInfo>, Boolean>(logs, i.hasNext());
        }
    }

    /**
     * Returns the tuple of diff of the given commit and the previous commit id.
     */
    public static Tuple<List<DiffInfo>, String> getDiffs(Git git, String id, Boolean... fetchContent) {
        RevWalk revWalk = new RevWalk(git.getRepository());
        try {
            revWalk.markStart(revWalk.parseCommit(git.getRepository().resolve(id)));
            List<RevCommit> commits = getCommitLogForDiffs(revWalk.iterator(), new ArrayList<RevCommit>());
            RevCommit revCommit = commits.get(0);

            if (commits.size() >= 2) {
                RevCommit oldCommit = commits.get(1);
                if (revCommit.getParentCount() >= 2) {
                    oldCommit = revCommit.getParents()[0];
                }
                if (fetchContent.length == 0) {
                    System.out.println("oldcommit -> " + oldCommit.getName() + " newcommit -> " + id);
                    return new Tuple<List<DiffInfo>, String>(getDiffs(git, oldCommit.getName(), id, true), oldCommit.getName());
                } else {
                    return new Tuple<List<DiffInfo>, String>(getDiffs(git, oldCommit.getName(), id, fetchContent[0]), oldCommit.getName());
                }
            } else {
                // initial commit
                TreeWalk treeWalk = new TreeWalk(git.getRepository());
                treeWalk.addTree(revCommit.getTree());
                List<DiffInfo> buffer = new ArrayList<DiffInfo>();
                while (treeWalk.next()) {
                    boolean fetch;
                    if (fetchContent.length == 0) {
                        fetch = true;
                    } else {
                        fetch = fetchContent[0];
                    }
                    if (!fetch) {
                        buffer.add(new DiffInfo(DiffEntry.ChangeType.ADD, null, treeWalk.getPathString(), "", ""));
                    } else {

                        buffer.add(new DiffInfo(DiffEntry.ChangeType.ADD, null, treeWalk.getPathString(), "",
                                getDiffContent(getContentFromId(git, treeWalk.getObjectId(0), false))));
                    }
                }
                return new Tuple<List<DiffInfo>, String>(buffer, "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the commit list of the specified branch.
     *
     * @param git      the Git object
     * @param revision the branch name or commit id
     * @param page     the page number (1-)
     * @param limit    the number of commit info per page. 0 (default) means unlimited.
     * @param path     filters by this path. default is no filter.
     * @return a tuple of the commit list and whether has next, or the error message
     */
    public static Tuple<List<CommitInfo>, Boolean> getCommitLog(final Git git, String revision, int page, int limit, final String path) {
        int fixedPage = 1;

        if (page > 0) {
            fixedPage = page;
        }

        RevWalk revWalk = new RevWalk(git.getRepository());
        ObjectId objectId = null;
        try {
            objectId = git.getRepository().resolve(revision);
            if (objectId == null) {
                return null;
            } else {
                revWalk.markStart(revWalk.parseCommit(objectId));
                if (!path.equals(".")) {
                    revWalk.setRevFilter(new RevFilter() {
                        @Override
                        public boolean include(RevWalk revWalk, RevCommit revCommit) throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
                            List<DiffInfo> diffInfos = getDiffs(git, revCommit.getName(), false).left;
                            for (DiffInfo info : diffInfos) {
                                if (info.newPath.startsWith(path)) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public RevFilter clone() {
                            return this;
                        }
                    });
                }
                return getCommitLogForLog(revWalk.iterator(), 0, new ArrayList<CommitInfo>(), limit, fixedPage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Tuple<List<CommitInfo>, Boolean>(null, false);
    }

    //初始化一个裸repo用来进行给人clone push
    public static boolean initBareRepo(String owner, String repository) {
        return initRepository(getRepositoryDir(owner, repository), true);
    }

    public static List<CommitInfo> viewRepoWithCommits(String owner, String repository, String revision, int page, String repopath) {
        try {
            Git git = Git.open(getRepositoryDir(owner, repository));
            Tuple<List<CommitInfo>, Boolean> commitLog = getCommitLog(git, revision, page, 30, repopath);
            List<CommitInfo> infos = commitLog.left;
            if (infos != null && infos.size() > 0) {
                return infos;
            } else {
                System.out.println("notfound");
                return new ArrayList<CommitInfo>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<CommitInfo>();
        }
    }
}
