package br.com.abner.springbootstart.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.abner.springbootstart.integrationtests.vo.PersonVO;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PersonEmbeddedVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("personVOList")
    private List<PersonVO> personList;

    public PersonEmbeddedVO() {
    }

    public List<PersonVO> getPersonList() {
        return personList;
    }

    public void setPersonList(List<PersonVO> personList) {
        this.personList = personList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((personList == null) ? 0 : personList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersonEmbeddedVO other = (PersonEmbeddedVO) obj;
        if (personList == null) {
            if (other.personList != null)
                return false;
        } else if (!personList.equals(other.personList))
            return false;
        return true;
    }

    

}
