<h1><a name="top">Available Mashup Intents:</a></h1>
<ul>
<?
foreach ( $mashupIntents as $mashupIntent ) {?>
	<li><a href="#<?= $mashupIntent->getPrimaryKey();?>"><?= $mashupIntent->getTitle();?></a></li>
<? }?>
</ul>
<hr>
<?
foreach ( $mashupIntents as $mashupIntent ) {?>
	<h2><a name="<?= $mashupIntent->getPrimaryKey();?>"><?= $mashupIntent->getTitle();?></a></h2> <a href="#top" style="float:right;">top</a>
	
	<p><strong>Intent Action:</strong> <?=$mashupIntent->getAction()?></p>
	<p><strong>Title:</strong>	<?=  $mashupIntent->getTitle();?></p>
	<p><strong>Description:</strong> </p>
	<p><?= $mashupIntent->getDescription();?></p>
	
	<? $extras = $mashupIntent->getExtras ();?>
	<h3>required Extras for this intent:</h3>
	<ul>
	<? foreach ( $extras as $extra ) {?>
		<? if ($extra->isMandatory() == "true"){ ?>
		<li>
			key: <?=  $extra->getName ();?><br/>
			type:<?=  $extra->getExtratype ()->getName ();?> (<?=  $extra->getExtratype ()->getDescription ();?> )<br/>
			<?= $extra->getRelationDescription ();?>
			
		<? }?>
		</li>
	<? }?>
	</ul>
	<h3>optional Extras for this intent:</h3>
	<ul>
	<? foreach ( $extras as $extra ) {?>
	<? if ($extra->isMandatory () == "false"){ ?>
		<li>
			key: <?=  $extra->getName ();?><br/>
			type:<?=  $extra->getExtratype ()->getName ();?> (<?=  $extra->getExtratype ()->getDescription ();?> )<br/>
			<?= $extra->getRelationDescription ();?>
		<? }?>
		</li>
	<? }?>
	</ul>
	<h3>Sample Implementation:</h3>
	<strong>for your manifest:</strong><br/>
	<blockquote>  	  	
		&lt;intent-filter&gt; <br/>
				&lt;action android:name="<?=$mashupIntent->getAction()?>" /&gt; <br/>
				&lt;category android:name="android.intent.category.DEFAULT" /&gt; <br/>
			&lt;/intent-filter&gt; <br/>
	</blockquote>
	<strong>to generate the Intent:</strong>
	<blockquote>  	  	
		i = new Intent("<?=$mashupIntent->getAction()?>");<br/>
		<? foreach ( $extras as $extra ) {?>
			i.putExtra("<?=  $extra->getName ();?>", <?=  $extra->getName ();?>);  //<?=  $extra->getName ();?> as a <?= $extra->getExtratype ()->getName ();?><br/>
		<? }?>
	</blockquote>
	<strong>to retrieve data from this Intent:</strong>
	<blockquote>
	  	 <? foreach ( $extras as $extra ) {?>
			<?= $extra->getExtratype ()->getName ();?> <?=  $extra->getName ()?> = 
			<?=  $extra->getExtratype ()->getJavaRetrieveCommand ()?>("<?=  $extra->getName ();?>", DEFAULT_<?=  strtoupper($extra->getName ())?>_VALUE);<br/>
		<? }?> 	
		
	</blockquote>
<?	}?>





