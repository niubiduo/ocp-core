package com.nbd.ocp.core.repository;

import com.nbd.ocp.core.repository.base.IOcpBaseDo;
import com.nbd.ocp.core.repository.constant.OcpCrudBaseDoConstant;
import com.nbd.ocp.core.repository.page.QueryPageBaseConstant;
import com.nbd.ocp.core.repository.page.QueryPageBaseVo;
import com.nbd.ocp.core.repository.utils.OcpBaseDaoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaPersistableEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OcpRepositoryImpl<T extends IOcpBaseDo, ID extends Serializable>
  extends SimpleJpaRepository<T, ID> implements OcpRepository<T, ID> {
    private final   JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final PersistenceProvider provider;


    public OcpRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        Assert.notNull(entityInformation, "JpaEntityInformation must not be null!");
        Assert.notNull(entityManager, "EntityManager must not be null!");
        this.entityInformation = entityInformation;
        this.em = entityManager;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }
    public OcpRepositoryImpl(Class<T> domainClass, EntityManager em) {
        this(JpaPersistableEntityInformation.getEntityInformation(domainClass, em), em);
    }

//    @Override
//    public List<T> getA() {
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
//        Root<T> root = cQuery.from(getDomainClass());
//        cQuery.select(root);
//        TypedQuery<T> query = em.createQuery(cQuery);
//        return query.getResultList();
//    }
    @Override
    public Page<T> page(final QueryPageBaseVo queryPageBaseVo){
        Pageable pageable = OcpBaseDaoUtils.generatedPage(queryPageBaseVo.getPageIndex(),queryPageBaseVo.getPageSize(),queryPageBaseVo.getSortMap());
        Page<T> page= findAll((Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            generatePredicate(predicates,queryPageBaseVo,root,criteriaBuilder);
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        },pageable);
        return page;
    }
    @Override
    public List<T> list( QueryPageBaseVo queryBaseVo) {
        List<T> list=findAll((Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            generatePredicate(predicates,queryBaseVo,root,criteriaBuilder);
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        });
        return list;
    }
    private void generatePredicate(List<Predicate> predicates,QueryPageBaseVo queryBaseVo,Root<T> root, CriteriaBuilder criteriaBuilder){

        Map<String,Object> parameters=queryBaseVo.getParameters();
        List<String> ids=queryBaseVo.getIds();
        List<Integer> statusList=queryBaseVo.getStatusList();
        if(ids!=null&&ids.size()>0){
            predicates.add(OcpBaseDaoUtils.generateStringIn(root,   criteriaBuilder, OcpCrudBaseDoConstant.DB_COLUMN_ID,ids));
        }
        if(statusList!=null&&statusList.size()>0){
            predicates.add(OcpBaseDaoUtils.generateIntegerIn(root,   criteriaBuilder, OcpCrudBaseDoConstant.DB_COLUMN_STATUS, statusList));
        }
        generateParametersPredicates(parameters,predicates,root,criteriaBuilder);
    }

    private void generateParametersPredicates(Map<String, Object> parameters, List<Predicate> predicates, Root<T> root, CriteriaBuilder criteriaBuilder){
        if(parameters!=null&&parameters.size()>1){
            final  String searchQu=parameters.get(QueryPageBaseConstant.VO_FIELD_FILTER_METHOD)==null?null:String.valueOf(parameters.get(QueryPageBaseConstant.VO_FIELD_FILTER_METHOD));
            if(StringUtils.isNotEmpty(searchQu)&&parameters!=null&&parameters.size()>0){
                Predicate predicateParameters = OcpBaseDaoUtils.generateQuery(root,   criteriaBuilder,searchQu, parameters);
                if(predicateParameters !=null){
                    predicates.add(predicateParameters);
                }
            }
        }
    }

}