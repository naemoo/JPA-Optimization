package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void memberTest() throws Exception {
		//given
		Member member = new Member("memberA");
		//when
		Long saveId = memberRepository.save(member);
		Member findMember = memberRepository.find(saveId);
		//then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
	}

	@Test
	public void findAll() throws Exception {
		//given
		Member member1 = new Member("A");
		Member member2 = new Member("B");
		memberRepository.save(member1);
		memberRepository.save(member2);
		//when
		List<Member> members = memberRepository.findAll();
		//then
		Assertions.assertThat(members.size()).isEqualTo(2);
	}

	@Test
	public void findByName() throws Exception {
		//given
		Member member = new Member("A");
		memberRepository.save(member);
		//when
		List<Member> members = memberRepository.findByName("A");
		//then
		Assertions.assertThat(members.get(0)).isEqualTo(member);
	}

}