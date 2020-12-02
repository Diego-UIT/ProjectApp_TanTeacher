package Model;

import java.io.Serializable;

public class courseItem implements Serializable {
    private  String url;
    private String title;
    private String fee;
    private String author;
    private String authorID;
    private String goal;
    private int percent;

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public courseItem() {
    }

    public courseItem(String url, String title, String author, String ID, float price, float discount) {
        this.url = url;
        this.title = title;
        this.author = author;
        this.ID = ID;
        this.price=price;
        this.discount=discount;
    }
    private String createAt;

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    private String desription;
    private String ID;
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    private String CategoryID;
    private float rating;
    private float price;
    private float discount;
    private String ranking;
    private String updateTime;

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDesription() {
        return desription;
    }

    public void setDesription(String desription) {
        this.desription = desription;
    }

    public String getID() {
        return ID;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private float totalVote;

    public courseItem(String url, String title, String fee, float rating) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.rating=rating;
    }
    public courseItem(String url, String title, String fee, String author, float rating, float price, float discount, float totalVote) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;

    }

    public courseItem(String url, String title, String fee, String author, float rating, float price, float discount, float totalVote,String goal, String desription) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
        this.goal=goal;
        this.desription=desription;
    }
    public courseItem(String url, String title, String fee, String author, float rating, float price, float discount, float totalVote,String goal, String desription,String ID,String categoryName,String CategoryID
    ,String rank, String updateTime) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
        this.goal=goal;
        this.desription=desription;
        this.ID=ID;
        this.categoryName=categoryName;
        this.CategoryID=CategoryID;
        this.ranking=rank;
        this.updateTime=updateTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTotalVote() {
        return totalVote;
    }

    public void setTotalVote(float totalVote) {
        this.totalVote = totalVote;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
