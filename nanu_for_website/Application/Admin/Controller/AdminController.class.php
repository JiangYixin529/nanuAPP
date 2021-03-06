<?php
namespace Admin\Controller;
use Think\Controller;
class AdminController extends CommonController{
    public function lst(){
    	$article=D('admin');
    	$count=$article->count();
    	$Page=new \Think\Page($count,3);
    	$show=$Page->show();
    	$list=$article->order('id desc')->limit($Page->firstRow.','.$Page->listRows)->select();
    	$this->assign('list',$list);
    	$this->assign('page',$show);
        $this->display();
    }
    public function add(){
    	$admin=D('admin');
    	if(IS_POST){
    		$date['username']=I('username');
    		$date['password']=md5(I('password'));
    		if($admin->create($date)){
    		if($admin->add()){
    			$this->success('添加用户成功',U('lst'));
    		}else{
    			$this->error('添加用户失败');
    		}	
    		}else{
    			$this->error($admin->getError());
    		}
    		return;
    	}
        $this->display();
    }
    public function edit(){
    	$admin=D('admin');
    	if(IS_POST){
    		$date['id']=I('id');
    		$date['username']=I('username');
    		$admins=$admin->find($date['id']);
    		$password=$admins['password'];
    		if(I('password')){
    			$date['password']=md5(I('password'));
    		}else{
    			$date['password']=$password;
    		}
    	if($admin->create($date)){
    		if($admin->save()){
    			$this->success('修改成功',U('lst'));
    		}else{
    			$this->error('修改失败');
    		}	
    		}else{
    			$this->error($admin->getError());
    		}
    		return;
    	}
    	$adminr=$admin->find(I('id'));
    	$this->assign('adminr',$adminr);
        $this->display();
    }
    public function del(){
    	$admin=D('admin');
    	$id=I('id');
    	if($id==1){
    		$this->error('本管理不可删除');
    	}else{
    	if($admin->delete(I('id'))){
    	$this->success('删除成功',U('lst'));	
    	}else{
    		$this->error('删除失败');
    	}
       
    }}
    public function sort(){
        $admin=D('admin');
        foreach ($_POST as $id=>$sort){
        	$admin->where(array('id'=>$id))->setField('sort',$sort);
        }
        $this->success('排序成功',U('lst'));
    }
    public function logout(){
    	session(null);
    	$this->success('退出成功',U('Login/index'));
    }
}