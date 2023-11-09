package ca.etsmtl.selenium.requests.payload.request;

import java.util.List;

import lombok.Data;

@Data
public class SeleniumCase {
    public int case_id;
    public String case_name;
    public List<SeleniumAction> actions;
}
