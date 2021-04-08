package jpabook.jpashop.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;

	@Test
	@DisplayName("회원 가입")
	public void memberJoin() throws Exception {
		//given
		Member member = new Member("kim");
		//when
		Long saveId = memberService.join(member);

		//then
		Assertions.assertEquals(member, memberRepository.find(saveId));
	}

	@Test
	public void duplicateMember() throws Exception {
		//given
		Member member1 = new Member("kim");
		Member member2 = new Member("kim");
		//when
		memberService.join(member1);

		//then
		Assertions.assertThrows(IllegalStateException.class, () -> memberService.join(member2));
	}

}