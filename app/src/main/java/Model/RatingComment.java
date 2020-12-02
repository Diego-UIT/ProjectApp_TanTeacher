package Model;

public class RatingComment {

    String userName;
    String commentContent;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    String avatar;
    Float numstar;

    public RatingComment(String userName, String commentContent, Float numstar, String avatar) {
        this.userName = userName;
        this.commentContent = commentContent;
        this.numstar = numstar;
        this.avatar=avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Float getNumstar() {
        return numstar;
    }

    public void setNumstar(Float numstar) {
        this.numstar = numstar;
    }
}
