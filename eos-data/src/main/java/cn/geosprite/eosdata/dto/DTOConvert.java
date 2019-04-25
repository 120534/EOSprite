package cn.geosprite.eosdata.dto;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:04 2019-4-22
 * @ Description：None
 * @ Modified By：
 */
public interface DTOConvert<S, T> {
    T doForward(S s);
    S doBackward(T t);
}
