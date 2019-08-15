// BookController.aidl
package com.virgil.study.aidl;
//要手动导入相应的包否则报错
import com.virgil.study.aidl.Book;
// Declare any non-default types here with import statements

interface BookController {
   List<Book> getBookList();
   void addBookInOut(inout Book book);
}
