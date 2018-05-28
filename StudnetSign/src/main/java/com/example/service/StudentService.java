package com.example.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.example.StudnetSignApplication;
import com.example.entity.Student;
@Service
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StudnetSignApplication.class)    
@WebAppConfiguration  
public class StudentService {
	private static Map<String,Student> students;
	//签到
	public static void Sign(String name) {
		initStudent();
		Student student =students.get(name);
		student.setState(true);
	}
	public static Map<String,Student>  selStudent(){
		initStudent();
		return students;
		
	} 
	//初始化学生信息
//	@Test
	public static void initStudent(){
		if(students==null) {
			students= new HashMap<String,Student>();
			students.put("李华", new Student("李华",false));
			students.put("张三", new Student("张三",false));
			students.put("rose", new Student("rose",false));
		}
	}
	//统计
	public static int count() {
		int i = 0;
		
		Collection<Student> stuList = students.values();
		Iterator<Student> iterator = stuList.iterator();
		while(iterator.hasNext()){
			Student student = iterator.next();
			i =  student.isSignState()? i+1:i;
		}
		return i;
	}
}
