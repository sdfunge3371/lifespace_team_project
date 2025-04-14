package com.lifespace.service;

import com.lifespace.dto.FavoriteSpaceDTO;
import com.lifespace.entity.FavoriteSpace;
import com.lifespace.entity.Member;
import com.lifespace.entity.Space;
import com.lifespace.mapper.FavoriteSpaceMapper;
import com.lifespace.repository.FavoriteSpaceRepository;
import com.lifespace.repository.MemberRepository;
import com.lifespace.repository.SpaceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteSpaceService {

    @Autowired
    private FavoriteSpaceRepository favoriteSpaceRepository;
    
    @Autowired
    private SpaceRepository spaceRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    /**
     * 取得會員收藏的所有空間
     * @param memberId 會員ID
     * @return 收藏空間DTO列表
     */
    public List<FavoriteSpaceDTO> getFavoriteSpacesByMemberId(String memberId) {
        List<FavoriteSpace> favoriteSpaces = favoriteSpaceRepository.findByMemberId(memberId);
        return FavoriteSpaceMapper.toFavoriteSpaceDTOList(favoriteSpaces);
    }

    /**
     * 判斷會員是否有收藏任何空間
     * @param memberId 會員ID
     * @return true表示已有收藏，false表示未收藏任何空間
     */
    public boolean hasFavoriteSpaces(String memberId) {
        return favoriteSpaceRepository.existsByMemberId(memberId);
    }

    /**
     * 判斷會員是否已收藏指定空間
     * @param memberId 會員ID
     * @param spaceId 空間ID
     * @return true表示已收藏，false表示未收藏
     */
    public boolean isFavoriteSpace(String memberId, String spaceId) {
        return favoriteSpaceRepository.findByMemberIdAndSpaceId(memberId, spaceId).isPresent();
    }

    /**
     * 新增收藏空間
     * @param memberId 會員ID
     * @param spaceId 空間ID
     * @return 新增的收藏空間DTO
     */
    @Transactional
    public FavoriteSpaceDTO addFavoriteSpace(String memberId, String spaceId) {
        // 檢查是否已收藏
        Optional<FavoriteSpace> existingFavorite = favoriteSpaceRepository.findByMemberIdAndSpaceId(memberId, spaceId);
        if (existingFavorite.isPresent()) {
            return FavoriteSpaceMapper.toFavoriteSpaceDTO(existingFavorite.get());
        }
        
        // 檢查會員和空間是否存在
        Optional<Member> member = memberRepository.findById(memberId);
        Optional<Space> space = spaceRepository.findById(spaceId);
        
        if (member.isEmpty() || space.isEmpty()) {
            return null;
        }
        
        // 新增收藏
        FavoriteSpace favoriteSpace = new FavoriteSpace(spaceId, memberId);
        favoriteSpace = favoriteSpaceRepository.save(favoriteSpace);
        
        return FavoriteSpaceMapper.toFavoriteSpaceDTO(favoriteSpace);
    }

    /**
     * 刪除收藏空間
     * @param memberId 會員ID
     * @param spaceId 空間ID
     * @return true表示刪除成功，false表示刪除失敗
     */
    @Transactional
    public boolean removeFavoriteSpace(String memberId, String spaceId) {
        Optional<FavoriteSpace> favoriteSpace = favoriteSpaceRepository.findByMemberIdAndSpaceId(memberId, spaceId);
        if (favoriteSpace.isPresent()) {
            favoriteSpaceRepository.delete(favoriteSpace.get());
            return true;
        }
        return false;
    }

    /**
     * 取得收藏空間的數量
     * @param memberId 會員ID
     * @return 收藏空間數量
     */
    public long getFavoriteSpaceCount(String memberId) {
        return favoriteSpaceRepository.countByMemberId(memberId);
    }

    /**
     * 取得會員收藏的空間ID列表
     * @param memberId 會員ID
     * @return 空間ID列表
     */
    public List<String> getFavoriteSpaceIds(String memberId) {
        return favoriteSpaceRepository.findSpaceIdsByMemberId(memberId);
    }
}