package br.com.abner.springbootstart.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.abner.springbootstart.integrationtests.vo.BookVO;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BookEmbeddedVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("bookVOList")
    private List<BookVO> bookList;

    public BookEmbeddedVO() {
    }

    public List<BookVO> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookVO> bookList) {
        this.bookList = bookList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookList == null) ? 0 : bookList.hashCode());
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
        BookEmbeddedVO other = (BookEmbeddedVO) obj;
        if (bookList == null) {
            if (other.bookList != null)
                return false;
        } else if (!bookList.equals(other.bookList))
            return false;
        return true;
    }

    

}
