package com.toy.toy.service;

import com.toy.toy.dto.LikeDto;
import com.toy.toy.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;


    //like를 처음에 board찾아올 때 동시에 찾아올 수 있으면 거기서 join fetch나 join이용해서 하고.

    //회원 아이디 필요.
    //
    public LikeDto update(Long id){
            likeRepository.findById(id).;
    }

}
