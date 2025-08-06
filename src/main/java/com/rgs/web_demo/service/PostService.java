package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Post;
import com.rgs.web_demo.dto.request.PostCreateRequest;
import com.rgs.web_demo.dto.request.PostUpdateRequestDto;
import com.rgs.web_demo.dto.response.PageResponse;
import com.rgs.web_demo.exception.BusinessException;
import com.rgs.web_demo.mapper.PostMapper;
import com.rgs.web_demo.repository.PostRepository;
import com.rgs.web_demo.vo.PostVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rgs.web_demo.exception.PostErrorCodes.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PageResponse<PostVo> getPosts(int page, int size, String keyword) {
        PageRequest pageable = PageRequest.of(page - 1, size);

        List<Post> posts = postRepository.findByKeyword(keyword, pageable);
        long total = postRepository.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) total / size);

        List<PostVo> postVos = posts.stream()
                .map(postMapper::toVo)
                .toList();

        return PageResponse.<PostVo>builder()
                .content(postVos)
                .totalPages(totalPages)
                .totalElements(total)
                .pageNumber(page)
                .build();
    }

    @Transactional
    public void createPost(PostCreateRequest request) {
        // Post post = request.toEntity();

        /*
        [comment-2]
        PostCreateRequest 코맨트 내용처럼 Entity 저장할 때 아래처럼 값 직접 선언해서 저장하면 좋을 것 같고
        만약 Entity 생성하는 위치가 여러곳인 경우에는 아래처럼 Entity 내부에 정적 팩토리 메서드 생성해서 사용하는게 더 권장되는 방식

        public static Post createPost(String title, String content, String authorEmail, String postType, Boolean isSecret) {
        return Post.builder()
            .title(title)
            .content(content)
            .authorEmail(authorEmail)
            .postType(postType)
            .isSecret(isSecret)
            .viewCount(0)
            .build();
        }
        */
        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .authorEmail(request.authorEmail())
                .postType(request.postType())
                .isSecret(request.isSecret())
                .viewCount(0)
                .build();

        postRepository.save(post);
    }

    /**
     * [comment-3]
     * 게시글 업데이트 기능은 아직 없는 것 같아서 추가하면서 JPA 쓰면 편한 기능 설명 해줄게
     */
    @Transactional
    public void updatePost(PostUpdateRequestDto updateDto) {

        /*
         * 1. Entity 조회 및 예외 처리
         * 조회된 Entity는 JPA 영속성 컨텍스트에 저장 (Persistence Context)
         * 여기에서 Persistence Context : JPA Entity의 상태를 추적하는 1차 캐시 공간
         */
        Post post = postRepository.findById(updateDto.id())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_POST));

        /*
         * 2. Entity 업데이트
         * Entity 의 update 메서드는 보통 Entity 내부에 선언하는게 좋아 JPA 의 더티 체킹 기능을 사용하는 목적에도 알맞고
         * 1번에서 저장된 영속성 컨텍스트 내부의 Entity 가 변경되면, JPA 가 이를 '더티(Dirty)' 상태로 관리하는데 아래 3번과 같은 기능 가능해
         * ( Dirty : 내부 필드는 변경 되었지만 아직 DB에 반영 되지 않은 상태 )
         */
        post.updatePost(
                updateDto.title(),
                updateDto.content(),
                updateDto.authorEmail(),
                updateDto.postType(),
                updateDto.isSecret()
        );

        /*
         * 3. 더티 체킹(Dirty Checking)과 자동 저장
         * JPA 더티 체킹 메커니즘
         * 1. @Transactional 시작 → 트랜잭션 시작, 영속성 컨텍스트 생성
         * 2. findById() 실행 → SELECT 쿼리 실행, Entity를 영속성 컨텍스트에 저장
         * 3. updatePost() 실행 → Entity 상태 변경 ( Dirty )
         * 4. 메서드 종료 시점 → 더티체킹 실행, UPDATE 쿼리 생성 및 실행
         * 5. @Transactional 종료 → 트랜잭션 커밋, 실제 DB 반영 ( Dirty Checking 으로 자동 update )
         */

        // 그래서 명시적으로 update Query 작성하지 않아도 됨

        // 그리고 지금 BusinessException 으로 잡아둔 예외 코드는 Member, Post 이렇게 까지 분리하는건 오버스팩 일 것 같긴 한데
        // 초기 세팅을 MemberErrorCodes로 잡아버려서 ㅋㅋㅋ 보통 MSA 구조에서 각 서비스에서 발생하는 에러 코드에 대한 체계 구축이나
        // 프론트엔드랑 일관된 에러 코드 체계 구축 이런 목적으로 저렇게 선언해서 넘겨주는게 훨씬 유지보수에 좋을 것 같어
        // 어떤 상황에 어떤 에러가 발생합니다. 뭐 이런 문서화 작업 했을 때도 좋고
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("해당 게시글을 찾을 수 없습니다.");
        }
        postRepository.deleteById(postId);
    }

    public PostVo getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
        return postMapper.toVo(post);
    }
}
