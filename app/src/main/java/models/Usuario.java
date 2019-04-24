package models;

import java.io.Serializable;
import java.util.regex.*;

public class Usuario implements Serializable {

    private int id;
    private String name;
    private String dateBirth;
    private String cpf;
    private String email;
    private String login;
    private String cellPhone;
    private String phone;
    private String zipCode;
    private String state;
    private String city;
    private String neighborhood;
    private String address;
    private String addressNumber;
    private String complement;
    private int serviceTerms;
    private String expirationTime;
    private int admin;
    private String date;
    private int status;
    private String lastUpdate;
    private String token;
    private String session;
    private String platformToken;

    public Usuario(){
        this.id = 0;
        this.name = "";
        this.dateBirth = "";
        this.cpf = "";
        this.email = "";
        this.login = "";
        this.cellPhone = "";
        this.phone = "";
        this.zipCode = "";
        this.state = "";
        this.city = "";
        this.neighborhood = "";
        this.address = "";
        this.addressNumber = "";
        this.complement = "";
        this.serviceTerms = 0;
        this.expirationTime = "";
        this.admin = 0;
        this.date = "";
        this.status = 0;
        this.lastUpdate = "";
        this.token = "";
        this.session = "";
        this.platformToken = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public int getServiceTerms() {
        return serviceTerms;
    }

    public void setServiceTerms(int serviceTerms) {
        this.serviceTerms = serviceTerms;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPlatformToken() {
        return platformToken;
    }

    public void setPlatformToken(String platformToken) {
        if(platformToken.length() == 32){
            this.platformToken = platformToken;
        } else{
            this.platformToken = "";
        }
    }

    // Functions
    public String getFirstName(){
        Pattern pattern = Pattern.compile("\\S+");
        Matcher matcher = pattern.matcher(name);
        String firstName = "";
        if(matcher.find()){
            firstName = matcher.group(0);
        }
        return firstName;
    }
}