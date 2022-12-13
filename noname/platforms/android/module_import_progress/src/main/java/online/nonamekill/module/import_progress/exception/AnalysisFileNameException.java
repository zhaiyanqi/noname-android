package online.nonamekill.module.import_progress.exception;

/**
 * 解析文件名称异常
 */
public class AnalysisFileNameException extends RuntimeException{

    private static final long serialVersionUID = 1391070054916830855L;

    public AnalysisFileNameException(final String name) {
        super(name);
    }

}
