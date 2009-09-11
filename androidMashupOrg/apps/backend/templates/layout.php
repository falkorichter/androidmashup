<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "
http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <?php include_http_metas() ?>
    <?php include_metas() ?>
    <?php include_title() ?>
    <link rel="shortcut icon" href="/favicon.ico" />
  </head>
  <body>
  <div id="header">
  	<h1>Administration</h1>
  </div>
  <div id="content">
	  <div id="navigation">
	  <ul>
	  	<li><?= link_to('developer', '/backend.php/developer') ?> </li>
	  	
	  	<li><?= link_to('applications', '/backend.php/application') ?>
	  		<ul><li><?= link_to('ApplicationHasIntent', '/backend.php/ApplicationHasIntent') ?></li></ul>
	  	</li>
	  	
	  	<li><?= link_to('intent', '/backend.php/intent') ?>
		  	<ul>
		  		<li><?= link_to('Intent handles Extra', '/backend.php/IntentHandlesExtra') ?></li> 
		  	</ul>
		</li>	  	 	
	  	<li><?= link_to('extras', '/backend.php/extra') ?>
		  	<ul>
		  		<li><?= link_to('Types of Extras', 'backend.php/extratype') ?></li>
			</ul>
	  	</li>
	  </ul>
	  </div>
	  <div id="admin"><?php echo $sf_content ?></div>
  </div>
  <div id="footer">
  	<p>the Mashup administration page 
		<?/*<form action="<?= url_to('backend.php/login') ?>" method="post"> 
			<input type="text" name="signin[username]" id="signin_username" />
			<input type="password" name="signin[password]" id="signin_password" />
			<input type="checkbox" name="signin[remember]" id="signin_remember" /></td> 
			<input type="submit" value="sign in" />
		</form>*/?>
	</p>
  </div>
  
      
  </body>
</html>
