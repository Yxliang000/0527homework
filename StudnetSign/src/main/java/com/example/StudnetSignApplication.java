package com.example;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.entity.Student;
import com.example.service.StudentService;

@SpringBootApplication
public class StudnetSignApplication {
	
	@Autowired
	private static StudentService ss;
	
	
	public static void main(String[] args) {
		SpringApplication.run(StudnetSignApplication.class, args);
		//查询并显示学生
		Map<String, Student> students = ss.selStudent();
		System.out.println(students);
		//rose签到
		ss.Sign("rose");
		//再查询一次
		Map<String, Student> students2 = ss.selStudent();
		System.out.println(students2);
		//输出统计
		int i = ss.count();
		System.out.println("已签到人数："+i);
	}
}
