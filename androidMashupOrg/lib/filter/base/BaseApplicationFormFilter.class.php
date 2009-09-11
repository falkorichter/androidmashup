<?php

require_once(sfConfig::get('sf_lib_dir').'/filter/base/BaseFormFilterPropel.class.php');

/**
 * Application filter form base class.
 *
 * @package    mashup
 * @subpackage filter
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormFilterGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseApplicationFormFilter extends BaseFormFilterPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'package'        => new sfWidgetFormFilterInput(),
      'name'           => new sfWidgetFormFilterInput(),
      'url'            => new sfWidgetFormFilterInput(),
      'icon'           => new sfWidgetFormFilterInput(),
      'apk_url'        => new sfWidgetFormFilterInput(),
      'developer_id'   => new sfWidgetFormPropelChoice(array('model' => 'Developer', 'add_empty' => true)),
    ));

    $this->setValidators(array(
      'package'        => new sfValidatorPass(array('required' => false)),
      'name'           => new sfValidatorPass(array('required' => false)),
      'url'            => new sfValidatorPass(array('required' => false)),
      'icon'           => new sfValidatorPass(array('required' => false)),
      'apk_url'        => new sfValidatorPass(array('required' => false)),
      'developer_id'   => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Developer', 'column' => 'id_developer')),
    ));

    $this->widgetSchema->setNameFormat('application_filters[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Application';
  }

  public function getFields()
  {
    return array(
      'id_application' => 'Number',
      'package'        => 'Text',
      'name'           => 'Text',
      'url'            => 'Text',
      'icon'           => 'Text',
      'apk_url'        => 'Text',
      'developer_id'   => 'ForeignKey',
    );
  }
}
