package com.liwq.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liwq.bookmanager.dto.BorrowDetailDTO;
import com.liwq.bookmanager.model.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 借阅记录Mapper
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    /**
     * 查询借阅详情列表
     */
    @Select("<script>" +
            "SELECT br.*, " +
            "u.username, u.real_name, " +
            "b.book_name, b.author, " +
            "bc.category_name " +
            "FROM borrow_records br " +
            "LEFT JOIN users u ON br.user_id = u.id " +
            "LEFT JOIN books b ON br.book_id = b.id " +
            "LEFT JOIN book_categories bc ON b.category_id = bc.id " +
            "WHERE 1=1 " +
            "<if test='userId != null'> AND br.user_id = #{userId} </if>" +
            "<if test='bookId != null'> AND br.book_id = #{bookId} </if>" +
            "<if test='status != null and status != \"\"'> AND br.status = #{status} </if>" +
            "ORDER BY br.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<BorrowDetailDTO> selectBorrowDetailList(@Param("userId") Long userId,
                                                   @Param("bookId") Long bookId,
                                                   @Param("status") String status,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);

    /**
     * 查询借阅详情总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM borrow_records br " +
            "WHERE 1=1 " +
            "<if test='userId != null'> AND br.user_id = #{userId} </if>" +
            "<if test='bookId != null'> AND br.book_id = #{bookId} </if>" +
            "<if test='status != null and status != \"\"'> AND br.status = #{status} </if>" +
            "</script>")
    Long countBorrowDetails(@Param("userId") Long userId,
                            @Param("bookId") Long bookId,
                            @Param("status") String status);

    /**
     * 根据ID查询借阅详情
     */
    @Select("SELECT br.*, " +
            "u.username, u.real_name, " +
            "b.book_name, b.author, " +
            "bc.category_name " +
            "FROM borrow_records br " +
            "LEFT JOIN users u ON br.user_id = u.id " +
            "LEFT JOIN books b ON br.book_id = b.id " +
            "LEFT JOIN book_categories bc ON b.category_id = bc.id " +
            "WHERE br.id = #{id}")
    BorrowDetailDTO selectBorrowDetailById(@Param("id") Long id);
}
